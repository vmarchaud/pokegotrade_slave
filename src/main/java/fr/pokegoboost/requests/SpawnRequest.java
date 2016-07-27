package fr.pokegoboost.requests;

import java.util.UUID;

import fr.pokegoboost.config.Account;
import lombok.Data;

@Data
public class SpawnRequest {

	private Account 	account;
	private UUID 		user;
}
