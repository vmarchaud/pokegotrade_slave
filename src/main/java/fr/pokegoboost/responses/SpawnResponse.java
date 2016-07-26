package fr.pokegoboost.responses;

import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SpawnResponse {

	private String	id;
	private Result	result;
}
