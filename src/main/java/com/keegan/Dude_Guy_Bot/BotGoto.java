package com.keegan.Dude_Guy_Bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class BotGoto extends Command {

	public BotGoto(IMessage m, IDiscordClient b) {
		super(m, b);
	}

	public void run() {
		/**
		 * Tells the bot to move to a given Voice Channel
		 */
		if (message.getAuthor().getVoiceChannel().isPresent()){
			IVoiceChannel voice_channel = message.getAuthor().getVoiceChannel().get();
			voice_channel.join();
		}else{
			displayMessage("You aren't in a Voice Channel.");
		}
	}

}
