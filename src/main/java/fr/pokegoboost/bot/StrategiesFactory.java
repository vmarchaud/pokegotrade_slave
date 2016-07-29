package fr.pokegoboost.bot;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.pokegoboost.bot.tasks.EvolveBestPokemonTask;
import fr.pokegoboost.bot.tasks.ITask;
import fr.pokegoboost.bot.tasks.ManageEggsTask;
import fr.pokegoboost.bot.tasks.TransferWeakPokemonTask;
import fr.pokegoboost.wrapper.Strategy;

public class StrategiesFactory {
	
	private static Map<EnumStrategy, ITask> tasks = Maps.newHashMap();
	
	static {
		tasks.put(EnumStrategy.TRANSFER_WEAK_POKEMON, new TransferWeakPokemonTask());
		tasks.put(EnumStrategy.MANAGE_EGG, new ManageEggsTask());
		tasks.put(EnumStrategy.EVOLVE_BEST_POKEMON, new EvolveBestPokemonTask());
	}
	
	public static List<Strategy> buildFrom(Map<EnumStrategy, Integer> request) {
		List<Strategy> strategies = Lists.newArrayList();
		request.entrySet().forEach(strategy -> {
			strategies.add(new Strategy(tasks.get(strategy.getKey()), strategy.getValue()));
		});
		return strategies;
	}
	
	public enum EnumStrategy {
		GET_POKEMON,
		CATCH_POKEMON,
		MANAGE_EGG,
		TRANSFER_WEAK_POKEMON,
		EVOLVE_BEST_POKEMON,
		DELETE_USELESS_ITEM;
	}
}
