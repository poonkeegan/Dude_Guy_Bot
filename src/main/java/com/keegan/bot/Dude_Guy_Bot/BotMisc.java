package com.keegan.bot.Dude_Guy_Bot;

import java.util.List;
import java.util.Random;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;


public class BotMisc extends Command {

	private static final String INSULTS_LIST[] = { "name Why do you even exist?", "Get out of my face name, you insult", 
													"name You suck, insult", "name No. Just. No." };
	private static final String WANG_INSULTS_LIST[] = { "name You're a cactus.", "name Hi cactus." };
	private static final String COMEBACK_LIST[] = { "baka", "idiot", "failure", "buttface", "cunt", 
													"weeb", "lolicon", "shotacon", "yaoi lover" };
	private static final String PRAISE_LIST[] = { "name You did a good job. SeemsOP", "name Nice Jobbu! DesuOP b", 
													"name Hurray, you did it! Clap1" };
	private static Random rngesus = new Random(System.currentTimeMillis()); // New RNG with time from epoch in ms as seed
	private static IUser author;
	private static List<IUser> mentionList;
	private String command;

	
	public BotMisc(String command) {
		this.command = command;
	}

	public void init(IMessage m, IDiscordClient b) {
		super.init(m, b);
		author = m.getAuthor();
		mentionList = m.getMentions();
	}

	public void run() {
		/**
		 * Implement commands for random misc./fun commands
		 */
		if (isAdmin() || isTester()) {
			// Get arguments passed
			String[] args = getArgs();
			
			if (command.equals("bully")) {
				// No Discord mention added: Bully the message sender
				if (args.length == 0) {
					displayMessage(author.mention(true) + " You didn't mention anyone, " + COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)] + "!");
				}
				// Mentioned invalid person
				else if (mentionList.isEmpty() && args.length > 0) {
					displayMessage(author.mention(true) + " That person doesn't exist, you " + COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)] + "!");
				}
				// Mentioned more than 1 person
				else if (mentionList.size() > 1) {
					displayMessage(author.mention(true) + " You're too savage! Just mention only one person you " + COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)] + ".");
				}
				// Bullying self
				else if (author.getName().equals(mentionList.get(0).getName())) {
					displayMessage(author.mention(true) + " Why do you want to bully yourself you masochistic " + COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)] + "??");
				}
				// Bully the mentioned person
				else {
					// Wang the cactus
					if (mentionList.get(0).getName().equals("Zache Bolté")) {
						displayMessage(WANG_INSULTS_LIST[rngesus.nextInt(WANG_INSULTS_LIST.length)].replace("name", mentionList.get(0).mention(true)));
					} else {
						System.out.println("Bullying " + mentionList.get(0).getName() + "(" + mentionList.get(0).mention(true) + ")");
						displayMessage(INSULTS_LIST[rngesus.nextInt(INSULTS_LIST.length)].replace("name", mentionList.get(0).mention(true)).replace("insult", COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)]));
					}
				}
			}
			else if (command.equals("praise")) {
				// No user mentioned: Praise the sender of the message
				if (args.length == 0) {
					displayMessage(PRAISE_LIST[rngesus.nextInt(PRAISE_LIST.length)].replace("name", author.mention(true)));
				}
				// No valid user mentioned
				else if (mentionList.isEmpty() && args.length > 0) {
					displayMessage("No mentioned user to praise TaigeiCry");
				}
				// Message sender is the same as the mentioned person: "Praise the sender"
				else if (author.getName().equals(mentionList.get(0).getName())) {
					displayMessage(author.mention(true) + "You have some nerve to send praise to yourself, you egotistical " + COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)]);
				}
				// Mention list contains more than one mentioned user
				else if (mentionList.size() > 1) {
					String praiseMessage = "";
					for (int user = 0; user < mentionList.size(); user++) {
						praiseMessage += mentionList.get(user).mention(true) + ", ";
					}
					praiseMessage += "You guys did a good job! SeemsOP";
					displayMessage(praiseMessage);
				}
				// Praise the mentioned user
				else {
					displayMessage(PRAISE_LIST[rngesus.nextInt(PRAISE_LIST.length)].replace("name", mentionList.get(0).mention(true)));
				}
			}

			// Return the URL of the mentioned user's profile picture
			else if (command.equals("icon")) {
				// No user mentioned
				if (args.length == 0) {
					displayMessage("No user mentioned.");
				}
				// Mentioned invalid person
				else if (mentionList.isEmpty() && args.length > 0) {
					displayMessage("Invalid user mentioned.");
				}
				// Mentioned more than 1 person
				else if (mentionList.size() > 1) {
					displayMessage("Please mention only one user at a time!");
				}
				// Get the mentioned user's icon URL
				else {
					bot.getGuilds().get(0).getUsersByName(mentionList.get(0).getName(), true).get(0).getAvatarURL();
				}
			}
			else {
				// Someone without bot permissions tried to execute a command
				displayMessage(author.mention(true) + " You don't have permission to use this command, you " + COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)]);
			}
		}
	}

	/**
	 * Return the command-specific help String to BotHelp
	 */
	public String getHelp() {
		String helpMessage = "";
		if (command.equals("bully")) {
			helpMessage += author.mention(true) + " How retarded are you?? Just mention someone with this command so I can bully them, " + COMEBACK_LIST[rngesus.nextInt(COMEBACK_LIST.length)] + "!";
		}
		else if (command.equals("praise")) {
			helpMessage += author.mention(true) + " Mention someone with this command so I can praise them!";
		}
		else if (command.equals("icon")) {
			helpMessage += author.mention(true) + " Mention someone to get the URL of their Discord profile picture";
		}
		return helpMessage;
	}

}
