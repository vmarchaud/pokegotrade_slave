package fr.pokegoboost.bot;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.pokegoboost.bot.tasks.GetArenaTask;
import fr.pokegoboost.bot.tasks.ITask;
import fr.pokegoboost.wrapper.Strategy;

public class StrategiesFactory {
	
	private static Map<EnumStrategy, ITask> tasks = Maps.newHashMap();
	
	static {
		tasks.put(EnumStrategy.GET_ARENA, new GetArenaTask());
	}
	
	public static List<Strategy> buildFrom(Map<EnumStrategy, Integer> request) {
		List<Strategy> strategies = Lists.newArrayList();
		request.entrySet().forEach(strategy -> {
			strategies.add(new Strategy(tasks.get(strategy.getKey()), strategy.getValue()));
		});
		return strategies;
	}
	
	public enum EnumStrategy {
		GET_ARENA;
	}
}
