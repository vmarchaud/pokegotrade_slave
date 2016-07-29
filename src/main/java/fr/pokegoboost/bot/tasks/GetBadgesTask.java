package fr.pokegoboost.bot.tasks;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;

import POGOProtos.Networking.Requests.Messages.CheckAwardedBadgesMessageOuterClass.CheckAwardedBadgesMessage;
import POGOProtos.Networking.Requests.Messages.EquipBadgeMessageOuterClass.EquipBadgeMessage;
import POGOProtos.Networking.Responses.CheckAwardedBadgesResponseOuterClass.CheckAwardedBadgesResponse;
import POGOProtos.Networking.Responses.EquipBadgeResponseOuterClass.EquipBadgeResponse;
import POGOProtos.Enums.BadgeTypeOuterClass.BadgeType;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.wrapper.Result;

public class GetBadgesTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		Map<Object, Object> results = new HashMap<Object, Object>();
		try {
			CheckAwardedBadgesMessage checkAwardedMsg = CheckAwardedBadgesMessage.newBuilder().build();
			ServerRequest serverRequestCheckAwarded = new ServerRequest(RequestType.CHECK_AWARDED_BADGES, checkAwardedMsg);
			instance.getGo().getRequestHandler().sendServerRequests(serverRequestCheckAwarded);
			
			CheckAwardedBadgesResponse CheckAwardedRes = CheckAwardedBadgesResponse.parseFrom(serverRequestCheckAwarded.getData());
			CheckAwardedRes.getAwardedBadgesList().forEach(badge -> {
				try {
					EquipBadgeMessage equipMsg = EquipBadgeMessage.newBuilder().setBadgeType(badge).setBadgeTypeValue(badge.getNumber()).build();
					ServerRequest serverRequestEquip = new ServerRequest(RequestType.EQUIP_BADGE, equipMsg);
					instance.getGo().getRequestHandler().sendServerRequests(serverRequestCheckAwarded);
					
					EquipBadgeResponse equipRes = EquipBadgeResponse.parseFrom(serverRequestEquip.getData());
					results.put(badge, equipRes.getResult());
				} catch (RemoteServerException e) {
					results.put(badge, Result.SERVER_ERROR);
				} catch (LoginFailedException e) {
					results.put(badge, Result.BAD_LOGIN);
				} catch (InvalidProtocolBufferException e) {
					results.put(badge, Result.SERVER_ERROR);
				}
				
			});
		} catch (RemoteServerException e) {
			results.put("CHECK_AWARDED_BADGE_ERROR", Result.SERVER_ERROR);
		} catch (LoginFailedException e) {
			results.put("CHECK_AWARDED_BADGE_ERROR", Result.BAD_LOGIN);
		} catch (InvalidProtocolBufferException e) {
			results.put("CHECK_AWARDED_BADGE_ERROR", Result.BAD_REQUEST);
		}
		return null;
	}

}
