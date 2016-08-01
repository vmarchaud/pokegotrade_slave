package fr.pokegoboost.bot;

import java.util.concurrent.atomic.AtomicBoolean;

import org.pogoapi.api.NetworkClient;
import org.pogoapi.api.NetworkRequest;
import org.pogoapi.api.objects.Location;

import POGOProtos.Networking.Requests.Messages.PlayerUpdateMessageOuterClass.PlayerUpdateMessage;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import fr.pokegoboost.config.CustomConfig;
import fr.pokegoboost.config.CustomLogger;
import lombok.Getter;
import lombok.Setter;

public class WalkingThread extends Thread {
	
	@Setter Location 				target;
	@Setter NetworkClient 			client;
	@Setter CustomLogger 			logger;
	@Getter AtomicBoolean			moving;
	@Setter Location				current;
	
	@Override
	public void run() {
		double dist = distance(current, target);
		int sections = (int) (dist / CustomConfig.SPEED);
		double changeLat = target.getLatitude() - current.getLatitude();
		double changeLon = target.getLongitude() - current.getLongitude();

		logger.log("Waiting " + sections + " seconds to travel " + (int) (dist) + " m");
		
		moving.set(true);
		for(int i = 0; i < sections; i++) {
			current.setLatitude(current.getLatitude() + changeLat * sections);
			current.setLongitude(current.getLongitude() + changeLon * sections);
				
			PlayerUpdateMessage request =  PlayerUpdateMessage.newBuilder()
						.setLatitude(current.getLatitude()).setLongitude(current.getLongitude()).build();
			// ignore response
			client.offerRequest(new NetworkRequest(RequestType.PLAYER_UPDATE, request, (result, data) -> { }));
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		current.setLongitude(target.getLongitude());
		current.setLatitude(target.getLatitude());
		moving.set(false);
	}

	public static double distance(Location loc1, Location loc2) {
		return distance(loc1.getLatitude(), loc2.getLatitude(), loc1.getLongitude(), loc2.getLongitude());
	}
	
	public static double distance(double lat1, double lat2, double lon1, double lon2) {

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		
		return Math.sqrt(Math.pow(6371 * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 1000, 2));
	}
}
