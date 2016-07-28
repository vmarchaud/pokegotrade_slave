package fr.pokegoboost.endpoints;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import fr.pokegoboost.Slave;
import fr.pokegoboost.requests.ComputeParkourRequest;
import fr.pokegoboost.requests.StartParkourRequest;

public class Order {
	
	private SocketIONamespace		api;
	private Slave					slave;
	
	public Order(SocketIONamespace api, Slave slave) {
		this.api = api;
		
		api.addEventListener("compute", ComputeParkourRequest.class, 
				(client, request, ack) -> handleParkourCompute(client, request, ack));
		api.addEventListener("parkour", StartParkourRequest.class, 
				(client, request, ack) -> handleParkourStart(client, request, ack));
	}
	
	
	public void handleParkourCompute(SocketIOClient client, ComputeParkourRequest request, AckRequest ackRequest) {
		
	}
	
	public void handleParkourStart(SocketIOClient client, StartParkourRequest request, AckRequest ackRequest) {
		
	}
}
