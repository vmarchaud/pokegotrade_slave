package fr.pokegotrade.slave.requests;

import lombok.Data;

@Data
public class EvolveRequest {
	
	private String token;
	private String pokemonId;
}
