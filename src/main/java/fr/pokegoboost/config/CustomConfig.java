package fr.pokegoboost.config;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

public class CustomConfig {
	public static int				SPEED = 25;
	public static int				RADIUS = 4;
	public static String			REDIS_HOST = "localhost";
	public static String			REDIS_PWD = "";
	public static int				REDIS_PORT = 8989;
	
	private transient static Gson		gson;
	
	public static CustomConfig load(Gson gso) throws IOException {
		gson = gso;
		return gson.fromJson(new FileReader(Paths.get("config.json").toFile()), CustomConfig.class);
	}
	
	public void save() throws JsonIOException, IOException {
		Writer writer = new FileWriter(Paths.get("config.json").toFile());
		writer.write(gson.toJson(this));
		writer.close();
	}
}
