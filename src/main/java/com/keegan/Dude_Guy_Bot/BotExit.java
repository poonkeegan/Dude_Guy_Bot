package com.keegan.Dude_Guy_Bot;


public class BotExit extends Command {


	public void run() {
		/**
		 * Has the bot exit the Voice Channel it currently is in
		 */
		if (!bot.getConnectedVoiceChannels().isEmpty()){
			bot.getConnectedVoiceChannels().get(0).leave();
		}
	}

}
