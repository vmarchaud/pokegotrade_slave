package fr.pokegoboost;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.pokegoboost.endpoints.Manager;

public class Slave {
	
	private SocketIOServer 	socket;
	private final Gson		gson = new GsonBuilder().serializeNulls()
			.setPrettyPrinting().create();
	
	public Slave() {
		Configuration config = new Configuration();
	    config.setHostname("0.0.0.0");
	    config.setPort(8042);

	    socket = new SocketIOServer(config);
	    new Manager(socket.addNamespace("api"), this);
	}
}
