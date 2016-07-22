package fr.pokegotrade.slave.responses;

import fr.pokegotrade.slave.wrapper.Pokemon;
import fr.pokegotrade.slave.wrapper.Result;
import lombok.Data;

@Data
public class EvolveResponse {
	
	private Pokemon pokemon;
	private int		candy;
	private int		experience;
	
	private Result	result;
}
