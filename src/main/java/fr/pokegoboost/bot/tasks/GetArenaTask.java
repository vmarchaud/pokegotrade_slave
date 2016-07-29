package fr.pokegoboost.bot.tasks;

import java.util.ArrayList;
import java.util.List;

import com.annimon.stream.Collectors;
import com.google.common.collect.Lists;
import com.google.common.geometry.MutableInteger;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;

import POGOProtos.Map.Fort.FortDataOuterClass.FortData;
import POGOProtos.Map.Fort.FortTypeOuterClass.FortType;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import POGOProtos.Networking.Requests.Messages.GetMapObjectsMessageOuterClass.GetMapObjectsMessage;
import POGOProtos.Networking.Responses.GetMapObjectsResponseOuterClass.GetMapObjectsResponse;
import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.config.Location;
import fr.pokegoboost.wrapper.Result;

public class GetArenaTask implements ITask {

	@Override
	public Object execute(PokeBot instance, Object... inputs) {
		Location current = instance.getParkour().get(instance.getIndex());
		
		GetMapObjectsMessage.Builder builder = GetMapObjectsMessage.newBuilder();

		builder = GetMapObjectsMessage.newBuilder()
					.setLatitude(current.getLattitude()).setLongitude(current.getLongitude());

		List<Long> cells = getCellIds(current, 9);
		for (Long cell : cells) {
			builder.addCellId(cell);
			builder.addSinceTimestampMs(System.currentTimeMillis() - 1000);
		}

		ServerRequest request = new ServerRequest(RequestType.GET_MAP_OBJECTS, builder.build());
		try {
			instance.getGo().getRequestHandler().sendServerRequests(request);
		} catch (RemoteServerException | LoginFailedException e1) {
			return Result.SERVER_ERROR;
		}
		
		GetMapObjectsResponse response = null;
		try {
			response = GetMapObjectsResponse.parseFrom(request.getData());
		} catch (InvalidProtocolBufferException e) {
			return Result.SERVER_ERROR;
		}
		
		List<FortData> arenas = Lists.newArrayList();
		response.getMapCellsList().stream()
				.flatMap(cell -> cell.getFortsList().stream())
				.filter(fort -> fort.getType() == FortType.GYM)
				.forEach(arena -> arenas.add(arena));
		

		return null;
	}
	
	public List<Long> getCellIds(Location loc, int width) {
		S2LatLng latLng = S2LatLng.fromDegrees(loc.getLattitude(), loc.getLongitude());
		S2CellId cellId = S2CellId.fromLatLng(latLng).parent(15);

		MutableInteger index = new MutableInteger(0);
		MutableInteger jindex = new MutableInteger(0);


		int level = cellId.level();
		int size = 1 << (S2CellId.MAX_LEVEL - level);
		int face = cellId.toFaceIJOrientation(index, jindex, null);

		List<Long> cells = Lists.newArrayList();

		int halfWidth = (int) Math.floor(width / 2);
		for (int x = -halfWidth; x <= halfWidth; x++) {
			for (int y = -halfWidth; y <= halfWidth; y++) {
				cells.add(S2CellId.fromFaceIJ(face, index.intValue() + x * size, jindex.intValue() + y * size).parent(15).id());
			}
		}
		return cells;
	}
}
