package fr.pokegoboost.responses;

import fr.pokegoboost.wrapper.PokemonWrapper;
import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class EvolveResponse {
	
	private PokemonWrapper pokemon;
	private int		candy;
	private int		experience;
	
	private Result	result;
}
