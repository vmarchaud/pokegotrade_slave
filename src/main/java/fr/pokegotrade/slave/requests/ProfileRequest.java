package fr.pokegotrade.slave.requests;

import lombok.Data;

@Data
public class ProfileRequest {
	
	private String 	token;
	private boolean	pokemonWanted;
}
