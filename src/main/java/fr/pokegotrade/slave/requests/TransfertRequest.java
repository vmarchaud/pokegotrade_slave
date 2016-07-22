package fr.pokegotrade.slave.requests;

import lombok.Data;

@Data
public class TransfertRequest {
	
	private String token;
	private String pokemonId;
}
