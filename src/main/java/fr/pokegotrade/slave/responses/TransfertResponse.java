package fr.pokegotrade.slave.responses;

import fr.pokegotrade.slave.wrapper.Result;
import lombok.Data;

@Data
public class TransfertResponse {
	
	private int		candy;
	
	private Result	result;
}