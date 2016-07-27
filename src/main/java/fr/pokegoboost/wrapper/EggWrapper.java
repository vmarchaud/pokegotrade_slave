package fr.pokegoboost.wrapper;

import lombok.Data;

@Data
public class EggWrapper {
	public enum State{
		IDLE,
		INCUBATE,
		HETCHED
	}
	
	private long 	id;
	private double 	distanceWalked;
	private double 	distanceTarget;
	private State 	state;
}
