package fr.pokegoboost.bot;

import java.util.List;
import java.util.Queue;

import org.pogoapi.api.NetworkClient;
import org.pogoapi.api.objects.Location;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import fr.pokegoboost.config.Account;
import fr.pokegoboost.config.CustomConfig;
import fr.pokegoboost.config.CustomLogger;
import fr.pokegoboost.wrapper.Strategy;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

public class PokeBot implements Runnable {

	private OkHttpClient	http = new OkHttpClient();
	private WalkingThread	walker = new WalkingThread();
	
	private Account 		account;
	private CustomLogger	logger;

	private final RethinkDB 		db = RethinkDB.r;
	
	@Getter
	NetworkClient			client;
	@Getter
	Queue<Action> 			queue = Queues.newConcurrentLinkedQueue();
	@Getter
	List<Location>			parkour	= Lists.newArrayList();
	@Getter
	int						index	= 0;
	@Getter @Setter
	boolean					running	= false;
	@Getter 
	List<Strategy>			strategies = Lists.newArrayList();
	@Getter
	volatile boolean		location;
	
	public PokeBot(Account account, CustomLogger logger) {
		this.account = account;
		this.logger = logger;
		
		walker.setLogger(logger);
	}
	
	public void run() {
		// this while should be always running
		try {
			while ( true ) {
				// move to the next position
				if (running) {
					Location loc = parkour.get(index);
					
					if (CustomConfig.TELEPORT || index == 0)
						client.updateLocation(loc);
					else {
						walker.setCurrent(parkour.get(index - 1));
						walker.setTarget(loc);
						walker.run();
					}
				}
				
				// When hes moving or not doing a parkour, respond to the requests
				while ( !running || walker.getMoving().get() ) {
					if (queue.poll() == null)
						continue ;
					Action action = queue.poll();
					Object result = action.getTask().execute(this, action.getInputs());
					action.getCallback().callback(result);
				}
				
				// if its a parkour, execute strategies
				if (running) {
					
					// check for executing strategies
					strategies.forEach(strategy -> {
						// if we are at the stop interval, execute the task
						if (index % strategy.getInterval() == 0)
							strategy.getTask().execute(this);
					});
					
					// go the next location
					index++;
					
					// we are at the end, clear all
					if (index == parkour.size()) {
						index = 0;
						running = false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void auth() {
		
	}
	
	public void shutdown() {
		logger.log("Shutdown request");
		walker.interrupt();
	}
	
	/**
	 * Get a connection to the database
	 * @return Connection object
	 */
	public Connection getDb() {
		return db.connection().db("test").hostname(CustomConfig.DB_HOST).user(CustomConfig.DB_USR, CustomConfig.DB_PWD).port(CustomConfig.DB_PORT).connect();
	}
}
