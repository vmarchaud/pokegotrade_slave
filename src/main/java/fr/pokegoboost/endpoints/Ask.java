package fr.pokegoboost.endpoints;

import java.util.Map;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import fr.pokegoboost.Slave;
import fr.pokegoboost.bot.Action;
import fr.pokegoboost.bot.tasks.GetProfileTask;
import fr.pokegoboost.bot.tasks.TransferPokemonTask;
import fr.pokegoboost.requests.DespawnRequest;
import fr.pokegoboost.requests.ProfileRequest;
import fr.pokegoboost.requests.SpawnRequest;
import fr.pokegoboost.requests.TransfertRequest;
import fr.pokegoboost.responses.DespawnResponse;
import fr.pokegoboost.responses.ProfileResponse;
import fr.pokegoboost.responses.SpawnResponse;
import fr.pokegoboost.responses.TransfertResponse;
import fr.pokegoboost.wrapper.BotWrapper;
import fr.pokegoboost.wrapper.ProfileWrapper;
import fr.pokegoboost.wrapper.Result;

public class Ask {
	
	private SocketIONamespace		api;
	private Slave					slave;
	
	public Ask(SocketIONamespace api, Slave slave) {
		this.api = api;
		
		api.addEventListener("profile", ProfileRequest.class, 
				(client, request, ack) -> handleProfile(client, request, ack));
		api.addEventListener("transfert", TransfertRequest.class, 
				(client, request, ack) -> handleTransfert(client, request, ack));
	}
	
	public void handleProfile(SocketIOClient client, ProfileRequest request, AckRequest ackRequest) {
		slave.queueAction(request.getBot(), new Action(new GetProfileTask(), null, (result) -> {
			if (result == null) 
				ackRequest.sendAckData(ProfileResponse.builder()
						.result(Result.BAD_REQUEST)
						.build());
			else 
				ackRequest.sendAckData(ProfileResponse.builder()
						.result(Result.DONE)
						.profile((ProfileWrapper) result)
						.build());
		}));
	}
	
	public void handleTransfert(SocketIOClient client, TransfertRequest request, AckRequest ackRequest) {
		slave.queueAction(request.getBot(), new Action(new TransferPokemonTask(), request.getPokemons().toArray(), (result) -> {
				ackRequest.sendAckData(TransfertResponse.builder()
						.result(Result.DONE)
						.transferts((Map<Long, Object>) result)
						.build());
		}));
	}
}
