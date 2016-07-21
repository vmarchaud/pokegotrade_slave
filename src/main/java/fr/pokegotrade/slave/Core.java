package fr.pokegotrade.slave;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.auth.PTCLogin;
import com.pokegoapi.exceptions.LoginFailedException;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;
import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;
import io.nats.client.Message;
import okhttp3.OkHttpClient;

public class Core {
	
	public static void main(String[] args) throws IOException, TimeoutException {		
		ConnectionFactory cf = new ConnectionFactory("nats://localhost:4222");
		Connection nc = cf.createConnection();
		
		nc.subscribe("getProfil", m -> {
			try {
				nc.publish(m.getReplyTo(), getProfil(new String(m.getData(), StandardCharsets.UTF_8)));
			} catch (IOException | LoginFailedException e) {
				e.printStackTrace();
			};
		});
		nc.subscribe("transfert", m -> {
			try {
				nc.publish(m.getReplyTo(),transfertPokemon(new String(m.getData(), StandardCharsets.UTF_8), 0));
			} catch (IOException e) {
				e.printStackTrace();
			};
		});
		nc.subscribe("evolve", m -> {
			try {
				nc.publish(m.getReplyTo(), evolvePokemon(new String(m.getData(), StandardCharsets.UTF_8), 0));
			} catch (IOException e) {
				e.printStackTrace();
			};
		});

		Message msg = nc.request("help", "help me".getBytes(), 1000);
		System.out.println(msg);
		
	}

	public static byte[] getProfil(String token) throws LoginFailedException{
		OkHttpClient http = new OkHttpClient();
		AuthInfo auth = new PTCLogin(http).login(token);
		PokemonGo go = new PokemonGo(auth, http);

		//renvoi que l'username
		return go.getPlayerProfile(true).getUsername().getBytes();
	}
	
	public static byte[] transfertPokemon(String token, long IDPokemon){
		OkHttpClient http = new OkHttpClient();
		AuthInfo auth = new PTCLogin(http).login(token);
		PokemonGo go = new PokemonGo(auth, http);
		
		byte[] result = null;
		for(Pokemon pokemon : go.getPokebank().getPokemons()){
			if(pokemon.getId() == IDPokemon){
				result = pokemon.transferPokemon().toString().getBytes();
			}
		}
		if(result == null)
			result = "NOT FOUND".getBytes();
		
		return result;
	}
	public static byte[] evolvePokemon(String token, long IDPokemon){
		OkHttpClient http = new OkHttpClient();
		AuthInfo auth = new PTCLogin(http).login(token);
		PokemonGo go = new PokemonGo(auth, http);
		
		byte[] result = null;
		for(Pokemon pokemon : go.getPokebank().getPokemons()){
			if(pokemon.getId() == IDPokemon){
				result = pokemon.evolve().toString().getBytes();
			}
		}
		if(result == null)
			result = "NOT FOUND".getBytes();
		
		return result;
	}
}
