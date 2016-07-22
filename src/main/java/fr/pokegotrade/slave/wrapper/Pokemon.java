package fr.pokegotrade.slave.wrapper;

import lombok.Data;

@Data
public class Pokemon {
	
	private String	id;
	private int		pokemonType;	
	private String	nickname;
	private int		cp;
	private boolean	favorite;
	private int		candys;
}
