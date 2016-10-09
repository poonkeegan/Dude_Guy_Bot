package com.keegan.bot.Dude_Guy_Bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.audio.events.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


public class Instance {

	private static volatile IDiscordClient client;
	private String token;
	private final AtomicBoolean reconnect = new AtomicBoolean(true);
	private HashMap<String, Command> bot_commands;
	private static IGuild guild;
	private static MusicPlayer audioPlayer;
	private static float initialVolume = 0.25f;
	private static File currSong = null;
	private static File prevSong = null;

	private String key;


	public Instance(String token, String key) {
		this.token = token;
		KEY = key;
	}
	public void initCommands(){
		/**
		 * Adds the bot's command list into a hashmap.
		 */
		bot_commands = new HashMap<String, Command>();
		bot_commands.put("audio", new BotAudio(audioPlayer));
		bot_commands.put("bully", new BotMisc("bully"));
		bot_commands.put("exit", new BotGoto("exit"));
		bot_commands.put("goto", new BotGoto("goto"));
		bot_commands.put("help", new BotHelp());
		bot_commands.put("icon", new BotMisc("icon"));
		bot_commands.put("kc", new BotKanColle());
		bot_commands.put("league", new BotLeague());
		bot_commands.put("praise", new BotMisc("praise"));
		bot_commands.put("roll", new BotGamble("roll"));
		bot_commands.put("toss", new BotGamble("toss"));
  	}

	public void login() throws DiscordException {
		client = new ClientBuilder().withToken(token).login();
		client.getDispatcher().registerListener(this);
		initCommands();
	}

	@EventSubscriber
	public void onReady(ReadyEvent event) {
		guild = client.getGuilds().get(0);
		audioPlayer = new MusicPlayer(guild);
		audioPlayer.setVolume(initialVolume); // Set the volume to 25% to protect ears
		initCommands();
		System.out.println("*** Discord bot armed ***");
	}

	@EventSubscriber
	public void onDisconnect(DiscordDisconnectedEvent event) {
		/**
		 * Handles bot disconnection, attempts to log it back in.
		 */
		CompletableFuture.runAsync(() -> {
			if (reconnect.get()) {
				System.out.println("Reconnecting bot");
				try {
					login();
				} catch (DiscordException e) {
					System.out.println("Failed to reconnect bot" + e);
				}
			}
		});
	}

	@EventSubscriber
	public void onMessage(MessageReceivedEvent event) {
		/**
		 * Handles when a message is sent through to the server.
		 */
		//System.out.println("Got message");
		try {
			// Gets the message from the event object NOTE: This is not the content of the message, but the object itself
			IMessage message = event.getMessage();
			// This is the content of the message rather then the object
			String content = message.getContent();
			System.out.println(content.startsWith(KEY));
			if (content.startsWith(KEY)) {
				// Remove the key from the message
				content = content.substring(key.length());
				String command = getCmd(message);
				// Check if valid command, run if so
				if (bot_commands.containsKey(command)){
					bot_commands.get(command).init(message, client);
					Thread command_thread = new Thread(bot_commands.get(command));
					command_thread.start();
				}
			}
			else if (content.contains("kek")) {
				// kek
				broadcast("kek", event.getMessage().getChannel());
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
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
		} catch (DiscordException e) {
			System.out.println("Logout failed: " + e);
		// Discord4J 2.6.1 Implement
		} catch (RateLimitException e) {
			System.out.println("Logout failed: " + e);
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


	/**
	 * Clear "Playing" status or set status to the currently-playing song name
	 */
	@EventSubscriber
	public void audioPlayerPaused(PauseStateChangeEvent event) {
		// AudioPlayer paused
		if (audioPlayer.isPaused()) {
			client.changeStatus(Status.empty());
			System.out.println("Paused");
		}
		// AudioPlayer unpaused
		else {
			try {
				client.changeStatus(Status.game(currSong.getName().substring(0, currSong.getName().indexOf('-'))));
				System.out.println("Unpaused");
			} catch (Exception e) {
				System.out.println("An error occurred: " + e.getMessage());
			}
		}
	}

	/**
	 * Clear "Playing" status upon finishing playback of a song
	 */
	@EventSubscriber
	public void audioFinishedPlaying(TrackFinishEvent event) {
		// Delete the file of the song that finished playing before the song that just stopped playing
		try {
			if (prevSong != null && audioPlayer.getPlaylistSize() > 1 && prevSong != currSong) {
				Files.delete(prevSong.toPath());
			}
			prevSong = currSong;
			currSong = null;
			System.out.println("Finished playing: " + prevSong.getName());
		} catch (Exception x) {
			System.out.println("Error deleting file: " + x.getMessage());
		}
		client.changeStatus(Status.empty());
	}

	/**
	 * Clear "Playing" status upon skipping a track
	 */
	@EventSubscriber
	public void audioSkipped(TrackSkipEvent event) {
		System.out.println("Track skipped");
		client.changeStatus(Status.empty());
		prevSong = currSong;
		currSong = null;
	}

	/**
	 * Clear "Playing" status upon clearing the playlist
	 */
	@EventSubscriber
	public void audioSkipped(PlaylistClearedEvent event) {
		System.out.println("Playlist Cleared");
		client.changeStatus(Status.empty());
	}

	/**
	 * Set "Playing" status to song name
	 */
	@EventSubscriber
	public void audioStartedPlaying(TrackStartEvent event) {
		try {
			BotAudio audioCommand = new BotAudio(audioPlayer);
			currSong = audioCommand.getCurrSong();
			audioCommand = null;
			System.out.println(currSong.getName().substring(0, currSong.getName().indexOf('-')));
			client.changeStatus(Status.game(currSong.getName().substring(0, currSong.getName().indexOf('-'))));
			System.out.println("Started playing: " + currSong.getName().substring(0, currSong.getName().indexOf('-')));
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

}
