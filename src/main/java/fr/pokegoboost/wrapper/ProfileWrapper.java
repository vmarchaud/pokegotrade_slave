package fr.pokegoboost.wrapper;

import com.pokegoapi.api.player.Team;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ProfileWrapper {
	
	private String 	username;
	private int		lvl;
	private long	experience;
	private long	nextLvl;
	private double	percentage;
	private int		stardust;
	private int		pokecoin;
	private Team	team;
	private int		itemStorage;
	private int		pokemonStorage;
}
