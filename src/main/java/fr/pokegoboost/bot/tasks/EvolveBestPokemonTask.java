package fr.pokegoboost.bot.tasks;

import java.util.HashMap;
import java.util.Map;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.pokemon.PokemonMetaRegistry;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.Result;

public class EvolveBestPokemonTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		Map<Long, Object>	results = new HashMap<Long, Object>();
		Map<PokemonId, Pokemon> pokemons = new HashMap<PokemonId, Pokemon>();

		for (Pokemon pokemon : instance.getGo().getInventories().getPokebank().getPokemons()) {
			if (!pokemons.containsKey(pokemon.getPokemonId())) {
				pokemons.put(pokemon.getPokemonId(), pokemon);
			}
		}

		for (Pokemon pokemon : instance.getGo().getInventories().getPokebank().getPokemons()){
			PokemonId hightestPokemonId = PokemonMetaRegistry.getHightestForFamily(pokemon.getPokemonFamily());

			if (hightestPokemonId != pokemon.getPokemonId() && PokemonMetaRegistry.getMeta(pokemon.getPokemonId()) != null &&
					instance.getGo().getInventories().getCandyjar().getCandies(pokemon.getPokemonFamily()) >= PokemonMetaRegistry.getMeta(pokemon.getPokemonId()).getCandyToEvolve()) {

				if (!pokemons.containsKey(hightestPokemonId) || pokemons.get(hightestPokemonId).getCp() < pokemon.getCp() * pokemon.getCpMultiplier()){
					try {
						results.put(pokemon.getId(), pokemon.evolve());
					} catch (LoginFailedException e) {
						results.put(pokemon.getId(), Result.BAD_LOGIN);
					} catch (RemoteServerException e) {
						results.put(pokemon.getId(), Result.SERVER_ERROR);
					}
				}
			}
		}
		return results;
	}

}
