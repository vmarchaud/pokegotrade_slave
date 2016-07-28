package fr.pokegoboost.bot.tasks;

import java.util.stream.Collectors;

import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.ItemWrapper;

public class GetItemsTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		return instance.getGo().getInventories().getItemBag().getItems().stream()
				.map(item -> ItemWrapper.builder()
								.id(item.getItemId().getNumber())
								.count(item.getCount())
								.build())
				.collect(Collectors.toList());
	}
}