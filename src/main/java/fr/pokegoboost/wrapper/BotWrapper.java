package fr.pokegoboost.wrapper;

import java.util.UUID;

import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.config.Account;
import fr.pokegoboost.config.CustomLogger;
import lombok.Data;

@Data
public class BotWrapper {
	
	private UUID			id;
	private UUID			user;
	private PokeBot			instance;
	private Thread			thead;
	private Account			account;
	private CustomLogger	logger;
}
