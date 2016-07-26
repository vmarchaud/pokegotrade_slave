package fr.pokegoboost.requests;

import fr.pokegoboost.config.Location;
import lombok.Data;

@Data
public class ProfileRequest {

	private boolean	pokemons;
	private boolean inventorys;
	private Location position;
}
