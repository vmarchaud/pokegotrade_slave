package fr.pokegoboost.bot.tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import POGOProtos.Networking.Responses.RecycleInventoryItemResponseOuterClass.RecycleInventoryItemResponse.Result;
import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.ItemWrapper;

public class DeleteItemTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		Map<ItemWrapper, Object> results = new HashMap<ItemWrapper, Object>();
		
		Arrays.asList(inputs).forEach(input -> {
			ItemWrapper itemw = (ItemWrapper) (input);
			try {
				Result result = instance.getGo().getInventories().getItemBag().removeItem(ItemId.forNumber(itemw.getId()), itemw.getCount());
				results.put(itemw, result);
			} catch (RemoteServerException e) {
				results.put(itemw, fr.pokegoboost.wrapper.Result.SERVER_ERROR);
			} catch (LoginFailedException e) {
				results.put(itemw, fr.pokegoboost.wrapper.Result.BAD_LOGIN);
			}
		});
		return null;
	}

}
