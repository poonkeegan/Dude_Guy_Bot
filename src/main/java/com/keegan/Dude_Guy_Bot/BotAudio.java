package com.keegan.Dude_Guy_Bot;

import java.util.NoSuchElementException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageBuilder;

public class BotAudio implements Command {
	
	public void run(IMessage message, IDiscordClient bot) {
		// Check for permissions

		MessageBuilder msg1 = new MessageBuilder(bot);
		msg1.withChannel(message.getChannel());
		msg1.withContent("joining");
		try {
			msg1.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isTester(message)) {
			try {
			IVoiceChannel curr_chn = message.getAuthor().getVoiceChannel().get();
			curr_chn.join();
			curr_chn.leave();
			displayMessage(message, bot, "Joined and left channel");
			} catch (NoSuchElementException e){
				displayMessage(message, bot, "You are not currently in a channel");
			}
		}else{
			displayMessage(message, bot, "No permission");
		}
		
	}

}
