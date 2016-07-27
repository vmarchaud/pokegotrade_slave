package fr.pokegoboost.bot.tasks;

import java.util.HashMap;
import java.util.Map;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse;
import fr.pokegoboost.bot.PokeBot;

public class TransferAllPokemonTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		Map<Long, Object>	results = new HashMap<Long, Object>();
		Map<PokemonId, Pokemon> pokemons = new HashMap<PokemonId, Pokemon>();
		for(Pokemon pokemon : instance.getGo().getInventories().getPokebank().getPokemons()) {

			if (pokemon.isFavorite())
				continue;

			if (pokemons.containsKey(pokemon.getPokemonId())) {
				if (pokemon.getCp() <= pokemons.get(pokemon.getPokemonId()).getCp()) {
					try {
						results.put(pokemon.getId(), pokemon.transferPokemon());
					} catch (LoginFailedException | RemoteServerException e) {
						results.put(pokemon.getId(),ReleasePokemonResponse.Result.FAILED);
					}
				} else {
					try {
						results.put(pokemons.get(pokemon.getPokemonId()).getId(), pokemons.get(pokemon.getPokemonId()).transferPokemon());
					} catch (LoginFailedException | RemoteServerException e) {
						results.put(pokemon.getId(),ReleasePokemonResponse.Result.FAILED);
					}
				}
			}
			else
				pokemons.put(pokemon.getPokemonId(), pokemon);
		}
		return results;
	}

}
