package com.keegan.Dude_Guy_Bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Instance {

	private static final Logger log = LoggerFactory.getLogger(Instance.class);
	private volatile IDiscordClient client;
	private String token;
	private final AtomicBoolean reconnect = new AtomicBoolean(true);
	private HashMap<String, Command> bot_commands;
	
	final static String KEY = "#";

	public Instance(String token) {
		this.token = token;
		initCommands();
	}
	
	public void initCommands(){
		/**
		 * Adds the bot's command list into a hashmap.
		 */
		bot_commands = new HashMap<String, Command>();
		bot_commands.put("audio", new BotAudio());
	}

	public void login() throws DiscordException {
		client = new ClientBuilder().withToken(token).login();
		client.getDispatcher().registerListener(this);
	}

	@EventSubscriber
	public void onReady(ReadyEvent event) {
		log.info("*** Discord bot armed ***");
	}

	@EventSubscriber
	public void onDisconnect(DiscordDisconnectedEvent event) {
		/**
		 * Handles bot disconnection, attempts to log it back in.
		 */
		CompletableFuture.runAsync(() -> {
			if (reconnect.get()) {
				log.info("Reconnecting bot");
				try {
					login();
				} catch (DiscordException e) {
					log.warn("Failed to reconnect bot", e);
				}
			}
		});
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		/**
		 * Handles when a message is sent through to the server.
		 */
		log.debug("Got message");
		try{

			// Gets the message from the event object NOTE: This is not the content of the message, but the object itself
			IMessage message = event.getMessage(); 
			// This is the content of the message rather then the object
			String content = message.getContent();
			
			if (content.startsWith(KEY)) {
				// Remove the key from the message
				content = content.substring(KEY.length());
				String command = getCmd(message);
				// Check if valid command, run if so
				if (bot_commands.containsKey(command)){
					bot_commands.get(command).run(message, client);
				}
			}
		}
		catch (Exception e) {
			log.debug(e.getMessage());
			broadcast(e.getMessage(), event.getMessage().getChannel());
		}

	}

	public void terminate() {
		/**
		 * Logs the bot out and stops it.
		 */
		reconnect.set(false);
		try {
			client.logout();
		} catch (HTTP429Exception | DiscordException e) {
			log.warn("Logout failed", e);
		}
	}

	public void broadcast(String message, IChannel channel){
		/**
		 * Have the bot display a message in the given channel
		 */
		try {
			new MessageBuilder(client).withChannel(channel).withContent(message).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getCmd(IMessage message){
		/**
		 * Returns the String command used to issue the command
		 * to the bot
		 */
		String content = message.getContent();
		// Check if parameter cutoff
		int end_of_cmd = content.indexOf(" ");
		// If there are no parameters, command is everything except the command
		if (end_of_cmd == -1){
			end_of_cmd = content.length();
		}
		return content.substring(content.indexOf(getKey(message)) + 1, end_of_cmd);
	}
	
	public static String getKey(IMessage message){
		/**
		 * Returns the key that is used to issue the command
		 */
		return String.valueOf(message.getContent().charAt(0));
	}
}