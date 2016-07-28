package fr.pokegoboost.requests;

import java.util.Map;
import java.util.UUID;

import fr.pokegoboost.bot.StrategiesFactory.EnumStrategy;
import lombok.Data;

@Data
public class StartParkourRequest {

	private UUID						bot;
	private Map<EnumStrategy, Integer> 	strategy;
}
