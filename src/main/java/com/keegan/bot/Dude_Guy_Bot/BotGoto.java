package com.keegan.bot.Dude_Guy_Bot;

import java.util.Iterator;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class BotGoto extends Command {

	public void run() {
		/**
		 * Tells the bot to move to a given Voice Channel
		 * or move to the sender's channel if none are given
		 */
		String arg = getArg();
		
		// No parameters, default 
		if (arg == null){
			if (message.getAuthor().getVoiceChannel().isPresent()){
				IVoiceChannel voice_channel = message.getAuthor().getVoiceChannel().get();
				try{
          voice_channel.join();
        } catch (Exception e){
          displayMessage(e.getMessage());
        }
			}else{
				displayMessage("You aren't in a Voice Channel.");
			}
		}
		// Tell bot to join a channel
		else{
			try{
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
}
