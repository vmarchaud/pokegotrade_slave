package fr.pokegoboost.wrapper;

import lombok.Data;

@Data
public class PokemonWrapper {
	
	private String	id;
	private int		pokemonType;	
	private String	nickname;
	private int		cp;
	private boolean	favorite;
	private int		candys;
}
