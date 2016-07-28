package fr.pokegoboost.bot.tasks;

import java.util.stream.Collectors;

import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.PokemonWrapper;

public class GetPokeBankTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		return instance.getGo().getInventories().getPokebank().getPokemons().stream()
			.map(pokemon -> 
					PokemonWrapper.builder()
						.id(String.valueOf(pokemon.getId()))
						.pokemonType(pokemon.getPokemonId().getNumber())
						.nickname(pokemon.getNickname())
						.cp(pokemon.getCp())
						.favorite(pokemon.isFavorite())
						.candys(pokemon.getCandy())
						.build())
			.collect(Collectors.toList());
	}
}