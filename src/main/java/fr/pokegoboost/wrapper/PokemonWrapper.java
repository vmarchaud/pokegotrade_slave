package fr.pokegoboost.wrapper;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PokemonWrapper {
	
	private String	id;
	private int		pokemonType;	
	private String	nickname;
	private int		cp;
	private boolean	favorite;
	private int		candys;
}
