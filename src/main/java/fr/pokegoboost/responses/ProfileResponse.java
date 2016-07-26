package fr.pokegoboost.responses;

import java.util.List;

import fr.pokegoboost.wrapper.Pokemon;
import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ProfileResponse {
	
	private String			name;
	private int				level;
	private int				experience;
	private List<Pokemon>	pokemons;
	private int				team;
	
	private Result			result;
}
