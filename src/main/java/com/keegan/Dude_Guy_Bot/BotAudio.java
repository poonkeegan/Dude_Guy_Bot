package com.keegan.Dude_Guy_Bot;

import java.util.NoSuchElementException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

public class BotAudio implements Command {

	public void run(IMessage message, IDiscordClient bot) {

		// Check for permissions
		if (isTester(message)) {
			try {
				// If the bot is in sender's channel, leave it
				IVoiceChannel curr_chn = message.getAuthor().getVoiceChannel().get();
				if (bot.getConnectedVoiceChannels().contains(curr_chn)){
					curr_chn.leave();
					displayMessage(message, bot, "Left channel");
				}
				// Otherwise join it
				else{
					
					curr_chn.join();
					displayMessage(message, bot, "Joined channel");
				}
			// Make sure the sender is in a channel
			} catch (NoSuchElementException e){
				displayMessage(message, bot, "You are not currently in a channel");
			}
		}else{
			displayMessage(message, bot, "No permission");
		}

	}

}
