package fr.pokegoboost.bot;

import fr.pokegoboost.bot.tasks.ITask;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Action {
	
	@Getter ITask		task;
	@Getter Object[]   	inputs;
	@Getter ICallback	callback;
	
	public interface ICallback {
		void callback(Object output);
	}
}
