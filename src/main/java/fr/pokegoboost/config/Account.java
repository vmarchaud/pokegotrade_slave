package fr.pokegoboost.config;

import lombok.Data;

@Data
public class Account {
	
	private EnumProvider 	provider;
	private String 			username;
	private String 			password;
	private String			token;
	private boolean			logging;
	
	public enum EnumProvider {
		GOOGLE, PTC;
	}
}
