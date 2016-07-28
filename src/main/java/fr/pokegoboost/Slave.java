package fr.pokegoboost;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;

import fr.pokegoboost.bot.Action;
import fr.pokegoboost.bot.PokeBot;
import fr.pokegoboost.config.Account;
import fr.pokegoboost.config.CustomConfig;
import fr.pokegoboost.config.CustomLogger;
import fr.pokegoboost.endpoints.Ask;
import fr.pokegoboost.endpoints.Manager;
import fr.pokegoboost.endpoints.Order;
import fr.pokegoboost.wrapper.BotWrapper;
import fr.pokegoboost.wrapper.Result;
import lombok.Getter;

public class Slave {
	
	private SocketIOServer 			socket;
	@Getter RedisClient 			redis;
	private final Gson				gson = new GsonBuilder().serializeNulls()
										.setPrettyPrinting().create();
	private Map<UUID, BotWrapper>	bots = new HashMap<UUID, BotWrapper>();
	
	public Slave() throws IOException {
		CustomConfig config = CustomConfig.load(gson);
		config.save();
	
		Configuration socketconfig = new Configuration();
		socketconfig.setHostname("0.0.0.0");
		socketconfig.setPort(8042);
		
	    redis = RedisClient.create(RedisURI.Builder.redis(CustomConfig.DB_HOST, CustomConfig.DB_PORT)
	    		.withPassword(CustomConfig.DB_PWD).withDatabase(1).build());

	    socket = new SocketIOServer(socketconfig);
	    new Manager(socket.addNamespace("api"), this);
	    new Order(socket.addNamespace("order"), this);
	    new Ask(socket.addNamespace("ask"), this);
	}
	
	/**
	 * Start a bot instance with this account
	 * @param Account : containing all data that is required to login
	 * @param UUUID : user id that owned this bot
	 * @return BotWrapper
	 */
	public BotWrapper		spawn(Account account, UUID user) {
		BotWrapper 			bot = new BotWrapper();
		CustomLogger	logger = new CustomLogger(account);
		PokeBot			pkb = new PokeBot(account, logger);
		
		bot.setAccount(account);
		bot.setLogger(logger);
		bot.setInstance(pkb);
		bot.setThead(new Thread(pkb));
		bot.setId(UUID.randomUUID());
		bot.setUser(user);
		
		bots.put(bot.getId(), bot);
		bot.getThead().start();
		return bot;
	}
	
	/**
	 * Instantly kill a bot
	 * @param UUID : bot id
	 * @return DONE or ERROR
	 */
	public Result		despawn(UUID uuid) {
		if (bots.containsKey(uuid)) {
			BotWrapper bot = bots.get(uuid);
			bot.getInstance().shutdown();
			bot.getThead().interrupt();
			bots.remove(uuid);
			return Result.DONE;
		}
		else
			return Result.ERROR;
	}
	
	/**
	 * Add a action to a bot that he will do async
	 * @param UUID : bot id
	 * @param Action : the action object that will be done
	 */
	public void			queueAction(UUID bot, Action action) {
		if (!bots.containsKey(bot))
			action.getCallback().callback(Result.BAD_REQUEST);
		else
			bots.get(bot).getInstance().getQueue().offer(action);
	}
}
