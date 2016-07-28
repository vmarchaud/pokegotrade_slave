package fr.pokegoboost.wrapper;

import lombok.Data;

@Data
public class EggWrapper {
	
	private long 	id;
	private double 	distanceWalked;
	private double 	distanceTarget;
	private State 	state;
	
	public enum State{
		IDLE,
		INCUBATE,
		HETCHED
	}
}
