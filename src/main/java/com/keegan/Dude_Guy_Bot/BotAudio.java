package com.keegan.Dude_Guy_Bot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

public class BotAudio implements Command {

	public BotAudio(){

	}
	public void run(IMessage message, IDiscordClient bot) {
		/**
		 * Implement commands for playing audio
		 */
		// Check for permissions
		if (isAdmin(message)) {
			// Get arguments passed

			String[] args = getArgs(message);
			// Handle no parameters
			if(args.length == 0){
				displayMessage(message, bot, "No parameters passed");
				String toDisp = "Correct usage " + Instance.getKey(message) + Instance.getCmd(message) + " \"parameters\"";
				displayMessage(message, bot, toDisp);
			} else if(args[0].equals("queue")){
				URL url = null;
				// Initialize the given url
				try {
					url = new URL(args[1]);
				} catch(ArrayIndexOutOfBoundsException e){
					displayMessage(message, bot, "No URL provided");
				} catch (MalformedURLException e) {
					displayMessage(message, bot, "Invalid URL");
				}
				if (url != null) {
					try {
						// Connect the bot to the correct voice channel
						if(bot.getConnectedVoiceChannels().isEmpty()){
							joinChannel(message, bot);
						}
						// Grab the audio channel of the bot to play the sound in
						AudioChannel audio_chn = bot.getConnectedVoiceChannels().get(0).getAudioChannel();
						// Queue up the url
						if (args[1].contains("youtube.com")){
							try {
								Process py = Runtime.getRuntime().exec("python ../youtube-dl -x --audio-format mp3 " + args[1]);
								displayMessage(message, bot, "Loading File");String input = null;
								BufferedReader in = new BufferedReader(new InputStreamReader(py.getInputStream()));
								String process_in = in.readLine();
								while (input != null){
									displayMessage(message, bot, process_in);
									process_in = in.readLine();
								}
								displayMessage(message, bot, "Done Loading");
								File dir = new File(System.getProperty("user.dir"));
								File music = null;
								
								for (File file : dir.listFiles()){
									if (file.getName().endsWith(".mp3")){
										music = file;
									}
								}
								audio_chn.queueFile(music);
								displayMessage(message, bot, music.getName());
								try{
									Files.delete(music.toPath());
								}catch (IOException x){
									displayMessage(message, bot, x.getMessage());
								}
							} catch (Exception e) {
								displayMessage(message, bot, e.getMessage());
							}
						}else{
							displayMessage(message, bot, url.toString());
							audio_chn.queueUrl(url);
						}
						displayMessage(message, bot, args[1] + " queued.");
					} catch (DiscordException e) {
						displayMessage(message, bot, "Audio channel problem" + e.getMessage());
					}
					
					
				}
			} // Handle pausing music
			else if (args[0].equals("pause")){
				// Bot must be in a channel to pause music
				if(bot.getConnectedVoiceChannels().isEmpty()){
					displayMessage(message, bot, "Not currently inside a voice channel.");
				}
				else {
					
					// Bot must have something queued to play
					try {
						if(bot.getConnectedVoiceChannels().get(0).getAudioChannel().getQueueSize() == 0){
							displayMessage(message, bot, "There is nothing playing");
						}
						else{
							bot.getConnectedVoiceChannels().get(0).getAudioChannel().pause();
						}
					} catch (DiscordException e) {
						e.printStackTrace();
					}
				}
			}else if (args[0].equals("play")){
				// Bot must be in a channel to pause music
				if(bot.getConnectedVoiceChannels().isEmpty()){
					displayMessage(message, bot, "Not currently inside a voice channel.");
				}
				else {
					
					// Bot must have something queued to play
					try {
						if(bot.getConnectedVoiceChannels().get(0).getAudioChannel().getQueueSize() == 0){
							displayMessage(message, bot, "There is nothing to play currently.");
						}
						else{
							bot.getConnectedVoiceChannels().get(0).getAudioChannel().resume();
						}
					} catch (DiscordException e) {
						e.printStackTrace();
					}
				}
			}else if (args[0].equals("clear")){
				// Bot must be in a channel to pause music
				if(bot.getConnectedVoiceChannels().isEmpty()){
					displayMessage(message, bot, "Not currently inside a voice channel.");
				}
				else {
					
					// Bot must have something queued to play
					try {
						if(bot.getConnectedVoiceChannels().get(0).getAudioChannel().getQueueSize() != 0){
							bot.getConnectedVoiceChannels().get(0).getAudioChannel().clearQueue();
							displayMessage(message, bot, "Queue Cleared.");
						}
					} catch (DiscordException e) {
						e.printStackTrace();
					}
				}
			}else if (args[0].equals("skip")){
				// Bot must be in a channel to skip music
				if(bot.getConnectedVoiceChannels().isEmpty()){
					displayMessage(message, bot, "Not currently inside a voice channel.");
				}
				else {
					
					// Bot must have something queued to play
					try {
						if(bot.getConnectedVoiceChannels().get(0).getAudioChannel().getQueueSize() == 0){
							displayMessage(message, bot, "There is nothing to skip currently.");
						}
						else{
							bot.getConnectedVoiceChannels().get(0).getAudioChannel().skip();
							displayMessage(message, bot, "Song Skipped.");
						}
					} catch (DiscordException e) {
						e.printStackTrace();
					}
				}
			}
			
		}else{
			displayMessage(message, bot, "No permission");
		}

	}

	public void joinChannel(IMessage message, IDiscordClient bot){
		/**
		 * Attempts to have the bot join the voice channel that user is in
		 */
		IVoiceChannel curr_chn = null;
		try{
			// Get channel of sender
			curr_chn = message.getAuthor().getVoiceChannel().get();
			// Connect to sender's channel
			if (!bot.getConnectedVoiceChannels().contains(curr_chn)){
				curr_chn.join();
			}
		} 
		// Make sure that there is a channel to join to
		catch (NoSuchElementException e){
			displayMessage(message, bot, "You are not currently in a valid channel to perform this command");
		}
	}

}
