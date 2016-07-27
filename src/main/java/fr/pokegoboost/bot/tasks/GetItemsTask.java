package fr.pokegoboost.bot.tasks;

import java.util.ArrayList;
import java.util.List;

import com.pokegoapi.api.inventory.Item;

import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.ItemWrapper;

public class GetItemsTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		List<ItemWrapper> items = new ArrayList<ItemWrapper>();
		
		for(Item item : instance.getGo().getInventories().getItemBag().getItems()){
			ItemWrapper itemw = ItemWrapper.builder()
					.id(item.getItemId().getNumber())
					.count(item.getCount())
					.build();
			items.add(itemw);
		}
		return items;
	}

}
