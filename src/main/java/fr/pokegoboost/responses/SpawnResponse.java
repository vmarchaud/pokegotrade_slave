package fr.pokegoboost.responses;

import java.util.UUID;

import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SpawnResponse {

	private UUID	id;
	private Result	result;
}
