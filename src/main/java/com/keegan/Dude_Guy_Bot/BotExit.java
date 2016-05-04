package com.keegan.Dude_Guy_Bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;

public class BotExit implements Command {

	public void run(IMessage message, IDiscordClient bot) {
		/**
		 * Has the bot exit the Voice Channel it currently is in
		 */
		if (!bot.getConnectedVoiceChannels().isEmpty()){
			bot.getConnectedVoiceChannels().get(0).leave();
		}
	}

}
