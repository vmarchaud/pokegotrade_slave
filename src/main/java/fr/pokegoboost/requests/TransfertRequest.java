package fr.pokegoboost.requests;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class TransfertRequest {
	
	private UUID			bot;
	private List<String> 	pokemons;
}
