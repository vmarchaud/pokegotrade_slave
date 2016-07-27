package fr.pokegoboost.bot;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.EggIncubator;
import com.pokegoapi.api.inventory.ItemBag;
import com.pokegoapi.api.inventory.Pokeball;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.pokemon.EggPokemon;
import com.pokegoapi.api.pokemon.HatchedEgg;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleAuthJson;
import com.pokegoapi.auth.GoogleAuthTokenJson;
import com.pokegoapi.auth.GoogleCredentialProvider;
import com.pokegoapi.auth.GoogleCredentialProvider.OnGoogleLoginOAuthCompleteListener;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import POGOProtos.Networking.Requests.Messages.LevelUpRewardsMessageOuterClass.LevelUpRewardsMessage;
import POGOProtos.Networking.Requests.Messages.PlayerUpdateMessageOuterClass.PlayerUpdateMessage;
import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;
import POGOProtos.Networking.Responses.EncounterResponseOuterClass.EncounterResponse.Status;
import POGOProtos.Networking.Responses.LevelUpRewardsResponseOuterClass.LevelUpRewardsResponse;
import POGOProtos.Networking.Responses.UseItemEggIncubatorResponseOuterClass.UseItemEggIncubatorResponse;
import fr.pokegoboost.config.Account;
import fr.pokegoboost.config.Account.EnumProvider;
import fr.pokegoboost.config.CustomConfig;
import fr.pokegoboost.config.CustomLogger;
import fr.pokegoboost.config.Location;
import lombok.Getter;
import okhttp3.OkHttpClient;

public class PokeBot implements Runnable {

	@Getter
	PokemonGo		go;
	private Account 		account;
	private CustomLogger	logger;
	private Location		spawn;
	
	@Getter
	Queue<Action> queue = new ConcurrentLinkedQueue<Action>();
	
	private OkHttpClient 	http = new OkHttpClient();
	
	public PokeBot(Account account, CustomLogger logger) {
		this.account = account;
		this.logger = logger;
	}
	
	public void run() {
		// fault tolerant
		while ( true ) {
			if (queue.poll() == null)
				continue ;
			Action action = queue.poll();
			Object result = action.getTask().execute(this, action.getInputs());
			action.getCallback().callback(result);
	
			// sleep to avoid spamming
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) { 
				// ignored, good night
			}
		}
	}
	
	public void auth() throws LoginFailedException, RemoteServerException {
		CredentialProvider auth = null;
		// loggin with PTC with credentials
		if (account.getProvider() == EnumProvider.PTC)
			auth = new PtcCredentialProvider(http, account.getUsername(), account.getRefreshToken());
		// loggin with google refresh token
		else if (account.getProvider() == EnumProvider.GOOGLE && account.getRefreshToken().length() > 0)
			auth = new GoogleCredentialProvider(http, account.getRefreshToken());
		
		go = new PokemonGo(auth, http);
	}

	public void transfertAllPokermon() throws LoginFailedException, RemoteServerException{
		Map<PokemonId, Pokemon> pokemons = new HashMap<PokemonId, Pokemon>();
		for(Pokemon pokemon : go.getInventories().getPokebank().getPokemons()) {

			if (pokemon.isFavorite())
				continue;

			if (pokemons.containsKey(pokemon.getPokemonId())) {
				if (pokemon.getCp() <= pokemons.get(pokemon.getPokemonId()).getCp()) {
					logger.log("Transfering pokemon " + pokemon.getPokemonId() + " : " + pokemon.transferPokemon());
				} else {
					logger.log("Transfering pokemon " + pokemons.get(pokemon.getPokemonId()).getPokemonId() + " : " + pokemons.get(pokemon.getPokemonId()).transferPokemon());
					pokemons.put(pokemon.getPokemonId(), pokemon);
				}
			}
			else
				pokemons.put(pokemon.getPokemonId(), pokemon);
		}
		
		for(Pokemon pokemon : go.getInventories().getPokebank().getPokemons()){
			PokemonId hightestPokemonId = PokemonMetaRegistry.getHightestForFamily(pokemon.getPokemonFamily());
			
			if (hightestPokemonId != pokemon.getPokemonId() && PokemonMetaRegistry.getMeta(pokemon.getPokemonId()) != null &&
				go.getInventories().getCandyjar().getCandies(pokemon.getPokemonFamily()) >= PokemonMetaRegistry.getMeta(pokemon.getPokemonId()).getCandyToEvolve()) {
				
				if(!pokemons.containsKey(hightestPokemonId) || pokemons.get(hightestPokemonId).getCp() < pokemon.getCp() * pokemon.getCpMultiplier()){
					EvolutionResult result = pokemon.evolve();
					logger.log("Evolving pokemon " + pokemon.getPokemonId() + " into " + result.getEvolvedPokemon().getPokemonId() + " " + result.getResult());
				}
			}
		}
	}

	public void capturePokemons(List<CatchablePokemon> list) throws LoginFailedException, RemoteServerException{
		for(CatchablePokemon pokemon : list) {

			if (pokemon.encounterPokemon().getStatus() == Status.ENCOUNTER_SUCCESS){
				go.getInventories().updateInventories(true);
				
				ItemBag bag = go.getInventories().getItemBag();

				Pokeball ball = null;
				if (bag.getItem(ItemId.ITEM_MASTER_BALL) != null && bag.getItem(ItemId.ITEM_MASTER_BALL).getCount() > 0)
					ball = Pokeball.MASTERBALL;
				else if (bag.getItem(ItemId.ITEM_ULTRA_BALL) != null && bag.getItem(ItemId.ITEM_ULTRA_BALL).getCount() > 0)
					ball = Pokeball.ULTRABALL;
				else if (bag.getItem(ItemId.ITEM_GREAT_BALL) != null && bag.getItem(ItemId.ITEM_GREAT_BALL).getCount() > 0)
					ball = Pokeball.GREATBALL;
				else if (bag.getItem(ItemId.ITEM_POKE_BALL) != null && bag.getItem(ItemId.ITEM_POKE_BALL).getCount() > 0)
					ball = Pokeball.POKEBALL;

				if (ball != null){
					CatchResult respondC = pokemon.catchPokemon(ball);
					
					logger.log("	" + respondC.getStatus() + ", " + pokemon.getPokemonId().name() + " using " + ball);
					
				}
				else
					logger.log("	NO POKEBALL for " + pokemon.getPokemonId().name());
			}
		}
	}

	public void getPokestops(Collection<Pokestop> pokestops) throws LoginFailedException, RemoteServerException{
		logger.log("Pokestop found : " + pokestops.size());
		
		List<Location> parkour = Parkour.buildLocationArrayFromPokestops(pokestops);
		
		double rawDistance = Parkour.getTotalParkour(parkour);
		logger.log("Raw parkour: " + (int)(rawDistance) + " m in " + (int)(rawDistance / CustomConfig.SPEED) + " secs");
		
		List<Location> bestParkour = Parkour.getBestParkour(Parkour.buildLocationArrayFromPokestops(pokestops));
		double optimisedDistance = Parkour.getTotalParkour(bestParkour);
		logger.log("Optimised parkour: " + (int)(optimisedDistance) + " m in " + (int)(optimisedDistance / CustomConfig.SPEED) + " secs");
		pokestops = Parkour.buildPokestopCollection(bestParkour, pokestops);
		
		int cpt = 0;
		
		for(Pokestop pokestop : pokestops) {
			cpt++;
			
			if (!pokestop.canLoot())
				run(pokestop.getLatitude(), pokestop.getLongitude());

			PokestopLootResult result = pokestop.loot();
			capturePokemons(go.getMap().getCatchablePokemon());

			logger.log("Pokestop " + cpt + "/" + pokestops.size() + " " + result.getResult() + ", XP: " + result.getExperience());
			

			if(cpt % 30 == 0) {
				transfertAllPokermon();
			}
			if (cpt % 10 == 0) {
				deleteUselessitem();
				manageEggs();
			}
		}
	}
	
	
	public void deleteUselessitem() throws RemoteServerException, LoginFailedException{
		go.getInventories().updateInventories(true);

		Map<ItemId, Integer> deleteItems = new HashMap<ItemId, Integer>();
		deleteItems.put(ItemId.ITEM_RAZZ_BERRY, 0);
		deleteItems.put(ItemId.ITEM_POTION, 0);
		deleteItems.put(ItemId.ITEM_SUPER_POTION, 0);
		deleteItems.put(ItemId.ITEM_HYPER_POTION, 15);
		deleteItems.put(ItemId.ITEM_REVIVE, 15);
		deleteItems.put(ItemId.ITEM_MAX_REVIVE, 15);
		deleteItems.put(ItemId.ITEM_POKE_BALL, 30);
		deleteItems.put(ItemId.ITEM_GREAT_BALL, 50);
		deleteItems.put(ItemId.ITEM_ULTRA_BALL, 50);
		deleteItems.put(ItemId.ITEM_MAX_POTION, 50);

		for(Entry<ItemId, Integer> entry : deleteItems.entrySet()){
			int countDelete = go.getInventories().getItemBag().getItem(entry.getKey()).getCount() - entry.getValue();
			if(countDelete > 0) {
				go.getInventories().getItemBag().removeItem(entry.getKey(), countDelete);
				logger.log(countDelete + " " + entry.getKey().name() + " deleted from inventory");
			}
		}
	}
	
	public void manageEggs() throws LoginFailedException, RemoteServerException {
		go.getInventories().updateInventories(true);
		
		for(HatchedEgg egg : go.getInventories().getHatchery().queryHatchedEggs()) {
			Pokemon pk = go.getInventories().getPokebank().getPokemonById(egg.getId());
			if (pk == null)
				logger.log("A egg has hetched");
			else
				logger.log(String.format("A egg has hetched : %s with cp : %d", pk.getPokemonId(), pk.getCp()));
		}
		
		go.getInventories().getHatchery().getEggs().stream()
		.filter(egg -> egg.isIncubate())
		.forEach(egg -> 
			logger.log(String.format("Egg %s is at %4.3f/%4.3f", Long.toUnsignedString(egg.getId()), egg.getEggKmWalked(), egg.getEggKmWalkedTarget())));
		
		List<EggIncubator> incubators = go.getInventories().getIncubators().stream()
				.filter(incubator -> !incubator.isInUse())
				.collect(Collectors.toCollection(ArrayList::new));
		logger.log("Currently have " + incubators.size() + " incubators available to incube eggs.");
		if (incubators.size() == 0)
			return ;
		
		List<EggPokemon> eggs = go.getInventories().getHatchery().getEggs().stream()
				.filter(egg -> egg.getEggIncubatorId() == null || egg.getEggIncubatorId().length() == 0)
				.sorted((left, right) -> Double.compare(left.getEggKmWalkedTarget(), right.getEggKmWalkedTarget()))
				.collect(Collectors.toCollection(ArrayList::new));
		logger.log("Currently have " + eggs.size() + "eggs available to be incubate.");
		if (eggs.size() == 0)
			return ;
		
		for(int i = 0; i < incubators.size(); i++) {
			if (eggs.get(i) == null)
				break ;
			UseItemEggIncubatorResponse.Result result = incubators.get(i).hatchEgg(eggs.get(i));
			logger.log("Trying to put an egg " + eggs.get(i).getEggKmWalkedTarget()  + " into the incubators result : " + result);
		}
	}
	
	public void getRewards(int cachedLvl) throws RemoteServerException, LoginFailedException {
		
		LevelUpRewardsMessage msg = LevelUpRewardsMessage.newBuilder().setLevel(cachedLvl).build(); 
		ServerRequest serverRequest = new ServerRequest(RequestType.LEVEL_UP_REWARDS, msg);
		go.getRequestHandler().sendServerRequests(serverRequest);
		
		LevelUpRewardsResponse response = null;
		try {
			response = LevelUpRewardsResponse.parseFrom(serverRequest.getData());
		} catch (InvalidProtocolBufferException e) {
			throw new RemoteServerException(e);
		}
		
		logger.log("Getting award for lvl " + (cachedLvl) + " with result : " + response.getResult());
		
	}

	public void run(double lat, double lon) throws LoginFailedException, RemoteServerException{
		double firstLat = go.getLatitude();
		double firstLon = go.getLongitude();
		double dist = Parkour.distance(lat, firstLat, lon, firstLon);
		int sections = (int) (dist / CustomConfig.SPEED);
		double changeLat = lat - firstLat;
		double changeLon = lon - firstLon;

		logger.log("Waiting " + (int) (dist / CustomConfig.SPEED) + " seconds to travel " + (int) (dist) + " m");

		for(int i = 0; i < sections; i++) {
			go.setLocation(firstLat + changeLat * sections, firstLon + changeLon * sections, 0);
			PlayerUpdateMessage request =  PlayerUpdateMessage.newBuilder()
					.setLatitude(go.getLatitude()).setLongitude(go.getLongitude()).build();

			go.getRequestHandler().sendServerRequests(new ServerRequest(RequestType.PLAYER_UPDATE, request));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}

		}
		go.setLocation(lat, lon, 0);
	}
	
	public class GoogleLoginOAuthCompleteListener implements OnGoogleLoginOAuthCompleteListener {

		@Override
		public void onInitialOAuthComplete(GoogleAuthJson auth) {
			logger.log("Waiting for the code " + auth.getUserCode() + " to be put in " + auth.getVerificationUrl());
		}

		@Override
		public void onTokenIdReceived(GoogleAuthTokenJson tokens) {
			account.setRefreshToken(tokens.getRefreshToken());
		}
	}
}
