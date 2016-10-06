package com.keegan.bot.Dude_Guy_Bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;


public class BotHelp extends Command {

	private static IMessage message;
	private static IDiscordClient client;

	public void init(IMessage m, IDiscordClient b) {
		super.init(m, b);
		message = m;
		client = b;
	}


	public void run() {

		/**
		 * Implement commands for individual command help
		 */

		String[] args = getArgs();

		// No command specified
		if (args.length == 0) {
			displayMessage("No bot command passed as a parameter");
		}
		else {
			if (args[0].equals("audio")) {
				String helpMessage = "List of parameters for the 'audio' command:\n\n";
				helpMessage += "'queue *youtube_Link*'\nQueue up a song from the specified YouTube URL\n\n";
				helpMessage += "'queue *mp3_Link*'\nQueue up a song from the specified URL that directly links to the .mp3 file\n\n";
				helpMessage += "'pause'\nPauses the currently playing song\n\n";
				helpMessage += "'play'\nResumes playback of the current song, if paused\n\n";
				helpMessage += "'repeat'\nDisplay a list of the last 5 songs played\n\n";
				helpMessage += "'repeat *song_number*'\nRepeat the song on the list at that number\n\n";
				helpMessage += "'volume *volume*'\nSets the volume output of the bot (server-side). *Volume* must be between 0-100 (inclusive)\n\n";
				helpMessage += "'skip'\nSkips the currently playing song";
				displayMessage(helpMessage);
			}
			else if (args[0].equals("bully")) {
				displayMessage(new BotMisc("bully").getHelp());
			}
			else if (args[0].equals("deck")) {
				displayMessage(new BotGamble("deck").getHelp());
			}
			else if (args[0].equals("exit")) {
				displayMessage(new BotExit().getHelp());
			}
			else if (args[0].equals("goto")) {
				displayMessage(new BotGoto().getHelp());
			}
			else if (args[0].equals("help")) {
				displayMessage(new BotHelp().getHelp());
			}
			else if (args[0].equals("kc")) {
				displayMessage(new BotKanColle().getHelp());
			}
			else if (args[0].equals("league")) {
				displayMessage(new BotLeague().getHelp());
			}
			else if (args[0].equals("praise")) {
				displayMessage(new BotMisc("praise").getHelp());
			}
			else if (args[0].equals("roll")) {
				displayMessage(new BotGamble("roll").getHelp());
			}
			else if (args[0].equals("rps")) {
				displayMessage(new BotGamble("rps").getHelp());
			}
			else if (args[0].equals("toss")) {
				displayMessage(new BotGamble("toss").getHelp());
			}
			else {
				displayMessage("Command not found. Please try again.");
			}
		}
	}

	/**
	 * Return the command-specific help String to BotHelp
	 */
	public String getHelp() {
		String helpMessage = "List of parameters for the 'help' command:\n\n";
		helpMessage += "'*command*'\nReturns a help String of the specified bot command, if it exists";
		return helpMessage;
	}

}
