package fr.pokegoboost.bot.tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.pokegoapi.api.pokemon.Pokemon;

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse;
import fr.pokegoboost.bot.PokeBot;

public class TransferPokemonTask implements ITask {

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		// each input is a pokemon to transfer
		Map<Long, Object>	results = new HashMap<Long, Object>();
		Arrays.asList(inputs).forEach(input -> {
			Long id = (Long) input;
			
			Pokemon pk = instance.getGo().getInventories().getPokebank().getPokemonById(id);
			if (pk != null) {
				try {
					// actual data
					results.put(id, pk.evolve());
				} catch (Exception e) {
					// the servers fucked up
					results.put(id, ReleasePokemonResponse.Result.FAILED);
				}
			}
			// pokemon doesnt exist in our inventory
			else
				results.put(id, ReleasePokemonResponse.Result.UNRECOGNIZED);
				
		});
		return results;
	}

}
