package fr.pokegoboost.bot.tasks;

import org.jgrapht.alg.util.Pair;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;

import POGOProtos.Networking.Requests.Messages.LevelUpRewardsMessageOuterClass.LevelUpRewardsMessage;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import POGOProtos.Networking.Responses.LevelUpRewardsResponseOuterClass.LevelUpRewardsResponse;
import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.Result;

public class GetRewardsTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {

		@SuppressWarnings("deprecation")
		int lvl = instance.getGo().getPlayerProfile(true).getStats().getLevel();
		
		Pair<Integer, Object> result = new Pair<Integer, Object>(lvl, null);
		
		LevelUpRewardsMessage msg = LevelUpRewardsMessage.newBuilder().setLevel(lvl).build(); 
		ServerRequest serverRequest = new ServerRequest(RequestType.LEVEL_UP_REWARDS, msg);

		LevelUpRewardsResponse response = null;
		try {
			instance.getGo().getRequestHandler().sendServerRequests(serverRequest);
			response = LevelUpRewardsResponse.parseFrom(serverRequest.getData());
			result.second = response.getResult();
		} catch (InvalidProtocolBufferException e) {
			result.second = Result.BAD_REQUEST;
		} catch (RemoteServerException e) {
			result.second = Result.SERVER_ERROR;
		} catch (LoginFailedException e) {
			result.second = Result.BAD_LOGIN;
		}
		return null;
	}

}
