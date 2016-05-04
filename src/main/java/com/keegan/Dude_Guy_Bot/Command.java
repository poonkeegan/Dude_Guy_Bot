package com.keegan.Dude_Guy_Bot;

import java.util.List;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageBuilder;

public interface Command {
	void run(IMessage message, IDiscordClient bot);
	
	default boolean hasCmdPerms(IMessage message, String role_name){
		/**
		 * Check if a message sender has a certain role on a server to be able
		 * to use the command.
		 */
		// Fetch the roles of the person who sent the message in the
		// server which the message was sent
		List<IRole> roles = message.getAuthor().getRolesForGuild(message.getGuild());
		
		boolean has_role = false;
		int role_index = 0;
		// Check if the person has the valid role
		while (!has_role && role_index < roles.size()){
			IRole role = roles.get(role_index);
			has_role = role.getName().equals(role_name);
			role_index++;
		}
		return has_role;
	}
	
	default boolean isAdmin(IMessage message){
		/**
		 * Check if a message sender has the role Admin on the server they sent it.
		 */
		return hasCmdPerms(message, "Admin");
	}
	
	default boolean isTester(IMessage message) {
		/**
		 * Check if a message sender has the role Bot tester on the server they sent it.
		 */
		return hasCmdPerms(message, "Bot tester");
	}
	
	default void displayMessage(IMessage cmd_msg, IDiscordClient bot, String message){
		MessageBuilder msg = new MessageBuilder(bot);
		msg.withChannel(cmd_msg.getChannel());
		msg.withContent(message);
		try {
			msg.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	default String[] getArgs(IMessage message){
		/**
		 * Fetch all arguments passed in the command
		 */
		String content = message.getContent();
		// Initialize to no arguments passed
		String[] args = new String[0];
		// Check if there is any spaces => there are arguments passed
		if (content.contains(" ")) {
			// Store them in an array
            args = content.substring(content.indexOf(' ') + 1).split(" ");
        }
		return args;
	}
	
}
