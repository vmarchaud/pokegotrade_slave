package fr.pokegoboost.responses;

import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TransfertResponse {
	
	private int		candy;
	
	private Result	result;
}