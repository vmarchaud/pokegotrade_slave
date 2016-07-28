package fr.pokegoboost.requests;

import java.util.List;
import java.util.UUID;

import fr.pokegoboost.config.Location;
import lombok.Data;

@Data
public class ComputeParkourRequest {
	
	private UUID			bot;
	private List<Location>	parkour;
	private boolean			optimize;
	private boolean			fromPokestop;
}
