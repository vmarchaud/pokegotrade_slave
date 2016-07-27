package fr.pokegoboost.responses;

import java.util.Map;

import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TransfertResponse {
	
	Map<Long, Object>	transferts;
	
	private Result	result;
}