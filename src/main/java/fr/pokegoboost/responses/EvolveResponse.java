package fr.pokegoboost.responses;

import fr.pokegoboost.wrapper.Pokemon;
import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class EvolveResponse {
	
	private Pokemon pokemon;
	private int		candy;
	private int		experience;
	
	private Result	result;
}
