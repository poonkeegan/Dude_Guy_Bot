package com.keegan.Dude_Guy_Bot;

import java.net.MalformedURLException;
import java.net.URL;
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
		if (isTester(message)) {
			// Get arguments passed

			String[] args = getArgs(message);
			// Handle no parameters
			if(args.length == 0){
				displayMessage(message, bot, "No parameters passed");
				String toDisp = "Correct usage " + Instance.getKey(message) + Instance.getCmd(message) + " \"parameters\"";
				displayMessage(message, bot, toDisp);
			} else if(args[0].equals("play")){
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
						joinChannel(message, bot);
						// Grab the audio channel of the bot to play the sound in
						AudioChannel audio_chn = bot.getConnectedVoiceChannels().get(0).getAudioChannel();
						// Queue up the url
						audio_chn.queueUrl(url);
						// Resume the queue
						audio_chn.resume();
					} catch (DiscordException e) {
						displayMessage(message, bot, "Audio channel problem" + e.getMessage());
					}
					
					
				}
			}
			// If the bot is in sender's channel, leave it
			
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
