package fr.pokegoboost.bot.tasks;

import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.ProfileWrapper;
import fr.pokegoboost.wrapper.Result;

public class GetProfileTask implements ITask {
	
	private int[] requiredXP = { 0, 1000, 3000, 6000, 10000, 15000, 21000, 28000, 36000, 45000, 55000, 65000, 75000,
            85000, 100000, 120000, 140000, 160000, 185000, 210000, 260000, 335000, 435000, 560000, 710000, 900000, 1100000,
            1350000, 1650000, 2000000, 2500000, 3000000, 3750000, 4750000, 6000000, 7500000, 9500000, 12000000, 15000000, 20000000 };

	public Object execute(PokeBot instance, Object... inputs) {
		try {
			instance.getGo().getPlayerProfile().updateProfile();
			PlayerProfile profile = instance.getGo().getPlayerProfile();
			
			int lvl = profile.getStats().getLevel();
			long cur = profile.getStats().getExperience() - requiredXP[lvl - 1];
			long next = requiredXP[lvl] - requiredXP[lvl - 1];
			
			return ProfileWrapper.builder().lvl(lvl)
					.experience(cur)
					.nextLvl(next)
					.percentage((double)cur / (double)next * 100.0)
					.stardust(profile.getCurrency(Currency.STARDUST))
					.itemStorage(profile.getItemStorage())
					.pokemonStorage(profile.getPokemonStorage())
					.pokecoin(profile.getCurrency(Currency.POKECOIN))
					.team(profile.getTeam())
					.build();
		} catch (RemoteServerException e) {
			return Result.SERVER_ERROR;
		} catch (LoginFailedException e) {
			return Result.BAD_LOGIN;
		} catch (InvalidCurrencyException e) {
			return Result.SERVER_ERROR;
		}
	}

}
