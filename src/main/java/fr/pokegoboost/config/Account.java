package fr.pokegoboost.config;

import lombok.Data;

@Data
public class Account {
	
	private EnumProvider 	provider;
	private String 			username;
	private String 			refreshToken;
	private String			accessToken;
	
	//config
	private boolean			logging;
	private Location		spawn;
	
	public enum EnumProvider {
		GOOGLE, PTC;
	}
}
