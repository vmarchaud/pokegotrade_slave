package fr.pokegoboost.bot.tasks;

import fr.pokegoboost.bot.PokeBot;

public interface ITask {
	
	/**
	 * The method that will be executed with the bot instance
	 * @param bot instance
	 * @return Object that represente the response
	 */
	public Object execute(PokeBot instance, Object... inputs);
}
