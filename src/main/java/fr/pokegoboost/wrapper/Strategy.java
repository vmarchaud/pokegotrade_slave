package fr.pokegoboost.wrapper;

import fr.pokegoboost.bot.tasks.ITask;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class Strategy {
	
	private ITask	task;
	private int		interval;
}
