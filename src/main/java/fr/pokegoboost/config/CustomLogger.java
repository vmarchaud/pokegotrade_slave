package fr.pokegoboost.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomLogger {
	
	private Account 			account;
	
	public void log(String input) {
		if (!account.isLogging()) return ;
		
		String log = String.format("[%s] on [%s] : %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"))
				, account.getUsername(), input);
		System.out.println(log);
	}
	
	public void important(String input) {
		String log = String.format("[%s] on [%s] : %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"))
				, account.getUsername(), input);
		System.out.println(log);
	}
}
