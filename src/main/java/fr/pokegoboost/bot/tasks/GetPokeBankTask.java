package fr.pokegoboost.bot.tasks;

import java.util.ArrayList;
import java.util.List;

import com.pokegoapi.api.pokemon.Pokemon;

import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.PokemonWrapper;

public class GetPokeBankTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		List<PokemonWrapper> pokemons = new ArrayList<PokemonWrapper>();
		
		for(Pokemon pokemon : instance.getGo().getInventories().getPokebank().getPokemons()){
			PokemonWrapper pokew = PokemonWrapper.builder()
					.id(String.valueOf(pokemon.getId()))
					.pokemonType(pokemon.getPokemonId().getNumber())
					.nickname(pokemon.getNickname())
					.cp(pokemon.getCp())
					.favorite(pokemon.isFavorite())
					.candys(pokemon.getCandy())
					.build();
			pokemons.add(pokew);
		}
		return pokemons;
	}

}
