package fr.pokegoboost.bot;

import java.util.List;
import java.util.Queue;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.SystemTimeImpl;

import fr.pokegoboost.config.Account;
import fr.pokegoboost.config.CustomConfig;
import fr.pokegoboost.config.CustomLogger;
import fr.pokegoboost.config.Location;
import fr.pokegoboost.wrapper.Strategy;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

public class PokeBot implements Runnable {

	private OkHttpClient	http = new OkHttpClient();
	private WalkingThread	walker = new WalkingThread();
	
	private Account 		account;
	private CustomLogger	logger;
	
	@Getter
	PokemonGo				go;
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
					
					if (CustomConfig.TELEPORT)
						go.setLocation(loc.getLattitude(), loc.getLongitude(), 1);
					else {
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
	
	public void auth() throws LoginFailedException, RemoteServerException {
		CredentialProvider auth = null;
		/*
		// loggin with PTC with credentials
		if (account.getProvider() == EnumProvider.PTC)
			auth = new PtcCredentialProvider(http, account.getUsername(), account.getRefreshToken());
		// loggin with google refresh token
		else if (account.getProvider() == EnumProvider.GOOGLE && account.getRefreshToken().length() > 0)
			auth = new GoogleCredentialProvider(http, account.getRefreshToken());
		*/
		go = new PokemonGo(auth, http, new SystemTimeImpl());
		walker.setGo(go);
	}
	
	public void shutdown() {
		logger.log("Shutdown request");
		walker.interrupt();
	}
}
