package com.keegan.Dude_Guy_Bot;

import java.util.List;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageBuilder;

public abstract class Command implements Runnable{

	IMessage message;
	IDiscordClient bot;
	
	public void init(IMessage m, IDiscordClient b){
		this.message = m;
		this.bot = b;
	}

	public abstract void run();
	
	public boolean hasCmdPerms(String role_name){
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
	
	public boolean isAdmin(){
		/**
		 * Check if a message sender has the role Admin on the server they sent it.
		 */
		return hasCmdPerms("Admin");
	}
	
	public boolean isTester() {
		/**
		 * Check if a message sender has the role Bot tester on the server they sent it.
		 */
		return hasCmdPerms("Bot tester");
	}
	
	public void displayMessage(String content){
		/**
		 * Displays a messasge to the same channel that the command was passed to
		 */
		MessageBuilder msg = new MessageBuilder(bot);
		msg.withChannel(message.getChannel());
		msg.withContent(content);
		try {
			msg.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] getArgs(){
		/**
		 * Fetch all arguments passed in the command and splits on whitespace
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
	
	public String getArg(){
		/**
		 * Fetch all arguments passed in the command and returns
		 * as a whole string
		 */
		String content = message.getContent();
		// Initialize to no arguments passed
		String arg = null;
		// Check if there is any spaces => there are arguments passed
		if (content.contains(" ")) {
			// Store them in an array
            arg = content.substring(content.indexOf(' ') + 1).trim();
        }
		return arg;
	}
	
	public void displayError(Exception e){
		/**
		 * Handles printing an error to the current channel
		 */
		for (StackTraceElement ste : e.getStackTrace()){
			displayMessage(ste.toString());
		}
		displayMessage(e.toString());
	}
}
