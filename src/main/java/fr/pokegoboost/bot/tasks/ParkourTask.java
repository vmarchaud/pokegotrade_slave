package fr.pokegoboost.bot.tasks;

import java.util.List;

import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.config.Location;
import fr.pokegoboost.wrapper.Result;
import fr.pokegoboost.wrapper.Strategy;

public class ParkourTask implements ITask {

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		if (!instance.isRunning())
			return Result.ERROR;
		
		List<Location>	locations = (List<Location>) inputs[0];
		List<Strategy>	strategies = (List<Strategy>) inputs[1];
		// reset to be sure
		instance.getParkour().clear();
		instance.getStrategies().clear();
		
		instance.getParkour().addAll(locations);
		instance.getStrategies().addAll(strategies);
		
		instance.setRunning(true);
		return Result.DONE;
	}

}
