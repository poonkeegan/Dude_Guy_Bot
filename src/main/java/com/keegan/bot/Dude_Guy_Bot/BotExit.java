package com.keegan.bot.Dude_Guy_Bot;


public class BotExit extends Command {


	public void run() {
		/**
		 * Has the bot exit the Voice Channel it currently is in
		 */
		if (!bot.getConnectedVoiceChannels().isEmpty()){
			bot.getConnectedVoiceChannels().get(0).leave();
		}
	}

	/**
	 * Return the command-specific help String to BotHelp
	 */
	public String getHelp() {
		String helpMessage = "List of parameters for the 'exit' command:\n\n";
		helpMessage += "'*no_parameters*'\nThe bot will leave the Voice Channel it is currently in (Only works if it is in a Voice Channel)";
		return helpMessage;
	}

}
