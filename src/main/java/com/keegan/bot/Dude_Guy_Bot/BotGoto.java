package com.keegan.bot.Dude_Guy_Bot;

import java.util.Iterator;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;

public class BotGoto extends Command {

	public void run() {
		/**
		 * Tells the bot to move to a given Voice Channel
		 * or move to the sender's channel if none are given
		 */
		String arg = getArg();
		
		// No parameters, try to join the voice channel of the sender of the message
		if (arg == null) {
			// Discord4J 2.6.1 implement
			try {
				IVoiceChannel voiceChannel = message.getAuthor().getConnectedVoiceChannels().get(0);
				voiceChannel.join();
			} catch (MissingPermissionsException e) {
				displayMessage("Insufficient permissions to join voice channel");
			} catch (Exception e) {
				displayMessage("You aren't in a Voice Channel.");
			}
		}
		// Tell bot to join a specified channel
		else {
			try {
				// What are the voice channels the bot can join
				IGuild curr_guild = bot.getGuilds().get(0);
				Iterator<IVoiceChannel> voice_chn_iter = curr_guild.getVoiceChannels().iterator();
				
				boolean chn_not_found = true;
				// Check if requested channel is in the set of channels available
				while (chn_not_found && voice_chn_iter.hasNext()){
					IVoiceChannel voice_chn = voice_chn_iter.next();
					if (voice_chn.getName().equals(arg)){
						chn_not_found = false;
						voice_chn.join();
					}
				}
				if (chn_not_found){
					displayMessage("No voice channel with name " + arg + " found.");
				}else{
					displayMessage("Joined " + arg);
				}
			}catch(Exception e){
				displayError(e);
			}
		}
	}

	/**
	 * Return the command-specific help String to BotHelp
	 */
	public String getHelp() {
		String helpMessage = "List of parameters for the 'goto' command:\n\n";
		helpMessage += "'*no_parameters*'\nThe bot will join the Voice Channel that you are currently in (Only works if you are in a Voice Channel)\n\n";
		helpMessage += "'*Channel_Name*'\nThe bot will join the specified Voice Channel (not case-sensitive)";
		return helpMessage;
	}

}
