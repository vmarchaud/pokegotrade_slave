package fr.pokegotrade.slave.responses;

import java.util.List;

import fr.pokegotrade.slave.wrapper.Pokemon;
import fr.pokegotrade.slave.wrapper.Result;
import lombok.Data;

@Data
public class ProfileResponse {
	
	private String			name;
	private int				level;
	private int				experience;
	private List<Pokemon>	pokemons;
	private int				team;
	
	private Result			result;
}
