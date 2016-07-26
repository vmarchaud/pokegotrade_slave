package fr.pokegoboost.endpoints;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import fr.pokegoboost.Slave;
import fr.pokegoboost.requests.DespawnRequest;
import fr.pokegoboost.requests.SpawnRequest;
import fr.pokegoboost.responses.DespawnResponse;
import fr.pokegoboost.responses.SpawnResponse;
import fr.pokegoboost.wrapper.Result;

public class Manager {
	
	private SocketIONamespace		api;
	private Slave					slave;
	
	public Manager(SocketIONamespace api, Slave slave) {
		this.api = api;
		
		api.addEventListener("spawn", SpawnRequest.class, 
				(client, request, ack) -> handleSpawn(client, request, ack));
		api.addEventListener("despawn", DespawnRequest.class, 
				(client, request, ack) -> handleDespawn(client, request, ack));
	}
	
	
	public void handleSpawn(SocketIOClient client, SpawnRequest request, AckRequest ackRequest) {
		SpawnResponse response = SpawnResponse.builder().id(null).result(Result.ERROR).build();
		ackRequest.sendAckData(response);
	}
	
	public void handleDespawn(SocketIOClient client, DespawnRequest request, AckRequest ackRequest) {
		DespawnResponse response = DespawnResponse.builder().result(Result.ERROR).build();
		ackRequest.sendAckData(response);
	}
}
