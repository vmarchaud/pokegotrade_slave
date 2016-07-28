package fr.pokegoboost.bot.tasks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;

import POGOProtos.Networking.Requests.Messages.CheckAwardedBadgesMessageOuterClass.CheckAwardedBadgesMessage;
import POGOProtos.Networking.Requests.Messages.EquipBadgeMessageOuterClass.EquipBadgeMessage;
import POGOProtos.Networking.Responses.CheckAwardedBadgesResponseOuterClass.CheckAwardedBadgesResponse;
import POGOProtos.Networking.Responses.EquipBadgeResponseOuterClass.EquipBadgeResponse;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import fr.pokegoboost.bot.PokeBot;

public class GetBadgesTask implements ITask{

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
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
				} catch (RemoteServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LoginFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidProtocolBufferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			});
		} catch (RemoteServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoginFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
