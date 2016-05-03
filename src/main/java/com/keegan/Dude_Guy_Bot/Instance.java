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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Instance {

    private static final Logger log = LoggerFactory.getLogger(Instance.class);

    private volatile IDiscordClient client;
    private String email;
    private String password;
    private String token;
    private final AtomicBoolean reconnect = new AtomicBoolean(true);
    final static String KEY = "#";

    public Instance(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Instance(String token) {
        this.token = token;
    }

    public void login() throws DiscordException {
        if (token == null) {
            client = new ClientBuilder().withLogin(email, password).login();
        } else {
            client = new ClientBuilder().withToken(token).login();
        }
        client.getDispatcher().registerListener(this);
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        log.info("*** Discord bot armed ***");
    }

    @EventSubscriber
    public void onDisconnect(DiscordDisconnectedEvent event) {
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
        log.debug("Got message");
        
        try
        {
        IMessage _message = event.getMessage(); //Gets the message from the event object NOTE: This is not the content of the message, but the object itself
        String _content = _message.getContent();
		IChannel channel = _message.getChannel(); //Gets the channel in which this message was sent.
			if (_content.startsWith(KEY)){
				String command = _content.toLowerCase();
				String[] _args = null;
				if (_content.contains(" "))
	            {
	                command = command.split(" ")[0];
	                _args = _content.substring(_content.indexOf(' ') + 1).split(" ");
	            }
				
				if (command.equals(KEY + "wish")){
					new MessageBuilder(this.client).withChannel(channel).withContent("Your wish is my command").build();
				}
				
				
			}
        }
		catch (Exception e)
        {
			log.debug(e.getMessage());
        }
		
		
		/*try {
			//Builds (sends) and new message in the channel that the original message was sent with the content of the original message.
			new MessageBuilder(this.client).withChannel(channel).withContent(message.getContent()).build();
		} catch (HTTP429Exception e) { //HTTP429Exception thrown. The bot is sending messages too quickly!
			System.err.print("Sending messages too quickly!");
			e.printStackTrace();
		} catch (DiscordException e) { //DiscordException thrown. Many possibilities.
			System.err.print(e.getErrorMessage()); //Print the error message sent by Discord
			e.printStackTrace();
		} catch (MissingPermissionsException e) { //MissingPermissionsException thrown. The bot doesn't have permission to send the message!
			System.err.print("Missing permissions for channel!");
			e.printStackTrace();
		}*/
        
    }

    public void terminate() {
        reconnect.set(false);
        try {
            client.logout();
        } catch (HTTP429Exception | DiscordException e) {
            log.warn("Logout failed", e);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}