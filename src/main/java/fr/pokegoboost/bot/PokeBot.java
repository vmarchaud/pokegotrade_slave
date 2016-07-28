package fr.pokegoboost.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import fr.pokegoboost.config.Account;
import fr.pokegoboost.config.Account.EnumProvider;
import fr.pokegoboost.config.CustomLogger;
import fr.pokegoboost.config.Location;
import fr.pokegoboost.wrapper.StrategyWrapper;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

public class PokeBot implements Runnable {

	private OkHttpClient	http = new OkHttpClient();
	private WalkingThread	walker = new WalkingThread();
	
	private Account 		account;
	private CustomLogger	logger;
	
	@Getter
	PokemonGo				go;
	@Getter
	Queue<Action> 			queue = Queues.newConcurrentLinkedQueue();
	@Getter
	List<Pokestop> 			pokestops = Lists.newArrayList();
	@Getter
	List<Location>			parkour	= Lists.newArrayList();
	@Getter
	int						index	= 0;
	@Getter
	Location				spawn;
	@Getter
	boolean					running	= false;
	@Getter 
	List<StrategyWrapper>	strategies = Lists.newArrayList();
	
	public PokeBot(Account account, CustomLogger logger) {
		this.account = account;
		this.logger = logger;
		this.spawn = account.getSpawn();
		
		walker.setLogger(logger);
	}
	
	public void run() {
		// this while should be always running
		try {
			while ( true ) {
				// move to the next position
				if (running) {
					walker.setTarget(parkour.get(index + 1));
					walker.run();
				}
				
				// When hes moving or not doing a parkour, respond to the requests
				while ( !running || walker.getMoving().get() ) {
					if (queue.poll() == null)
						continue ;
					Action action = queue.poll();
					Object result = action.getTask().execute(this, action.getInputs());
					action.getCallback().callback(result);
				}
				
				// if its a parkour, loot or execute strategies
				if (running) {
					// if we are collecting pokestop, actually collect them
					if (!pokestops.isEmpty())
						pokestops.get(index).loot();
					
					// check for executing strategies
					strategies.forEach(strategy -> {
						// if we are at the stop interval, execute the task
						if (index % strategy.getInterval() == 0)
							strategy.getTask().execute(this);
					});
					
					// go the next location
					index++;
					
					// we are at the end, clear all and start from 0
					if (index == parkour.size()) {
						running = false;
						index = 0;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		walker.setGo(go);
	}
	
	public void shutdown() {
		logger.log("Shutdown request");
		walker.interrupt();
	}
}
