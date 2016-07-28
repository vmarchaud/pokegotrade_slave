package fr.pokegoboost.bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.alg.HamiltonianCycle;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.pokegoapi.api.map.fort.Pokestop;

import fr.pokegoboost.config.Location;

public class ParkourUtils {

	public static List<Location> getBestParkour(List<Location> locations) {
		SimpleWeightedGraph<Location, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Location, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		for(int i = 0; i < locations.size(); i++){
			graph.addVertex(locations.get(i));
		}
		for(int i = 0; i < locations.size(); i++){
			for(int j = i + 1; j < locations.size(); j++){
				graph.setEdgeWeight(graph.addEdge(locations.get(i), locations.get(j)), distance(locations.get(i), locations.get(j)));
			}
		}
		
		return HamiltonianCycle.getApproximateOptimalForCompleteGraph(graph);
	}
	
	public static double getTotalParkour(List<Location> locations){
		double result = 0;
		for(int i = 0; i < locations.size() - 1; i++){
			result += distance(locations.get(i), locations.get(i + 1));
		}
		return result;
	}
	
	public static List<Location> buildLocationArrayFromPokestops(Collection<Pokestop> pokestops){
		return pokestops.stream()
				.map(pokestop -> new Location(pokestop.getLatitude(), pokestop.getLongitude()))
				.collect(Collectors.toList());
	}

	public static double distance(Location loc1, Location loc2) {
		return distance(loc1.getLattitude(), loc2.getLattitude(), loc1.getLongitude(), loc2.getLongitude());
	}
	
	public static double distance(double lat1, double lat2, double lon1, double lon2) {

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		
		return Math.sqrt(Math.pow(6371 * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 1000, 2));
	}

	public static Collection<Pokestop> buildPokestopCollection(List<Location> parkour, Collection<Pokestop> pokestops) {
		Collection<Pokestop> result = new ArrayList<Pokestop>();
		
		for (Location loc : parkour) {
			result.add(pokestops.stream()
					.filter(pt -> pt.getLatitude() == loc.getLattitude() && pt.getLongitude() == loc.getLongitude())
					.findAny().get());
		}
		return result;
	}
}
