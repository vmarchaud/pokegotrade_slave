package fr.pokegoboost.bot;

import java.util.concurrent.atomic.AtomicBoolean;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;

import POGOProtos.Networking.Requests.Messages.PlayerUpdateMessageOuterClass.PlayerUpdateMessage;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import fr.pokegoboost.config.CustomConfig;
import fr.pokegoboost.config.CustomLogger;
import fr.pokegoboost.config.Location;
import lombok.Getter;
import lombok.Setter;

public class WalkingThread extends Thread {
	
	@Setter Location 				target;
	@Setter PokemonGo 				go;
	@Setter CustomLogger 			logger;
	@Getter AtomicBoolean			moving;
	
	@Getter @Setter
	volatile Location				location;
	
	@Override
	public void run() {
		double dist = ParkourUtils.distance(location, target);
		int sections = (int) (dist / CustomConfig.SPEED);
		double changeLat = target.getLattitude() - location.getLattitude();
		double changeLon = target.getLongitude() - location.getLongitude();

		logger.log("Waiting " + sections + " seconds to travel " + (int) (dist) + " m");
		
		moving.set(true);
		for(int i = 0; i < sections; i++) {
			
			try {
				location.setLattitude(location.getLattitude() + changeLat * sections);
				location.setLongitude(location.getLongitude() + changeLon * sections);
				
				PlayerUpdateMessage request =  PlayerUpdateMessage.newBuilder()
						.setLatitude(location.getLattitude()).setLongitude(location.getLongitude()).build();
				go.getRequestHandler().sendServerRequests(new ServerRequest(RequestType.PLAYER_UPDATE, request));
			
			} catch (RemoteServerException | LoginFailedException e1) { 
				// ignore this
			}
			
			// move every second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		go.setLocation(location.getLattitude(), location.getLongitude(), 1);
		moving.set(false);
	}

}
