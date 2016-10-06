package com.keegan.bot.Dude_Guy_Bot;

import java.util.List;
import java.util.Random;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.handle.impl.obj.PrivateChannel;


public class BotGamble extends Command {

	private static Random rngesus = new Random(System.currentTimeMillis()); // New RNG with time from epoch in ms as seed
	private static IUser author;
	private static List<IUser> mentionList;
	private String command;

	public BotGamble(String type) {
		command = type;
	}


	public void init(IMessage m, IDiscordClient b) {
		super.init(m, b);
		author = m.getAuthor();
		mentionList = m.getMentions();
	}

	public void run() {
		/**
		 * Implement commands for commands that involve gambling
		 */
		
		String[] args = getArgs();

		// Dice roll
		if (command.equals("roll")) {
			// No special die specified: Roll a standard 6-sided die
			if (args.length == 0) {
				displayMessage(author.mention(true) + " rolled a " + rollDice(6) + " on a 6-sided die");
			}
			else {
				// More than 2 integer parameters passed
				if (args.length > 2) {
					displayMessage("Too many parameters passed!");
				}
				// Roll a custom multi-sided die multiple times
				else if (args.length == 2) {
					if (Integer.parseInt(args[0]) >= 6 && Integer.parseInt(args[1]) > 0) {
						int rolls[] = rollDice(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
						String rollsMessage = author.mention(true) + " Rolled ";
						for (int roll = 0; roll < Integer.parseInt(args[1]); roll++) {
							if (!(roll == Integer.parseInt(args[1]) - 1)) {
								rollsMessage += "a " + rolls[roll] + ", ";
							}
							else {
								rollsMessage += " and a " + rolls[roll] + 1;
							}
						}
						rollsMessage += " on a " + Integer.parseInt(args[0]) + "-sided dice";
						displayMessage(rollsMessage);
					}
				}
				// Roll a custom multi-sided die
				else if (args.length == 1) {
					try {
						displayMessage(author.mention(true) + " Rolled a " + rollDice(Integer.parseInt(args[0])) + 1 + " on a " + Integer.parseInt(args[0]) + "-sided die");
					} catch (Exception e) {
						
					}
				}
				// Roll a 6-sided die if no integer parameters are passed
				else {
					displayMessage(author.mention(true) + " Rolled a " + rollDice(6) + " on a 6-sided die");
				}
			}
		}

		// Draw a card from a deck of 52 cards
		else if (command.equals("deck")) {
			displayMessage("soon™");
		}

		// Rock paper scissors
		else if (command.equals("rps")) {
			// No parameters passed
			if (args.length < 1) {
				displayMessage("You didn't mention someone to play rock-paper-scissors against!");
			}
			// Mentioned invalid person
			else if (mentionList.isEmpty() && args.length > 0) {

			}
			// Play rps against mentioned user
			else {
				displayMessage("soon™");
			}
		}

		// Toss a coin for a heads or tails result
		else if (command.equals("toss")) {
			displayMessage(author.mention(true) + " tossed a " + coinToss());
		}

	}

	/**
	 * Roll a numDiceFaces-sided Die once and return the result
	 */
	private int rollDice(int numDiceFaces) {
		return rngesus.nextInt(numDiceFaces) + 1;
	}

	/**
	 * Roll a numDiceFaces-sided Die numRolls times
	 */
	private int[] rollDice(int numDiceFaces, int numRolls) {
		int rolls[] = new int [numRolls];
		for (int roll = 0; roll < numRolls; roll++) {
			rolls[roll] = rollDice(numDiceFaces);
		}
		return rolls;
	}

	/**
	 * Randomly generate "rock", "paper", or "scissors"
	 */
	private String rps() {
		//getOrCreatePMChannel();
		return "";
	}

	/**
	 * Randomly generate "heads" or "tails;
	 */
	private String coinToss() {
		int toss = rngesus.nextInt(2);
		String tossResult = "Heads";
		switch(toss) {
			// Heads
			case 0: break;
			// Tails
			case 1: tossResult = "Tails";
					break;
		}
		return tossResult;
	}

	/**
	 * Return the command-specific help String to BotHelp
	 */
	public String getHelp() {
		String helpMessage = "";
		if (command.equals("roll")) {
			helpMessage += "List of parameters for the 'roll' command:\n\n";
			helpMessage += "'*no_parameters*'\nRoll a 6-sided die\n\n";
			helpMessage += "'*num_die_faces*'\nRoll a custom multi-sided die with the specified # of faces (Must be >= 6)\n\n";
			helpMessage += "'*num_die_faces num_times*'\nRoll a multi-sided die with the specified # of faces (Must be >= 6) the specified number of times";
		}
		else if (command.equals("deck")) {
			helpMessage += "List of parameters for the 'deck' command:\n\n";
			helpMessage += "'draw'\nDraw a card from the top of the deck, return its string representation, and put it at the bottom\n\n";
			helpMessage += "'shuffle'\nShuffle the deck";
		}
		else if (command.equals("rps")) {
			helpMessage += "List of parameters for the 'rps' command:\n\n";
			helpMessage += "'*@mention*' Plays rock-paper-scissors once against the mentioned user Reply to the bot's message with your choice of \"rock\", \"paper\", \"scissors\" (not case-sensitive) and the bot will output the result after having received both players' choices.";
		}
		else if (command.equals("toss")) {
			helpMessage += "List of parameters for the 'toss' command:\n\n";
			helpMessage += "'*no_parameters*' Tosses a coin and randomly gives a result of \"Heads\" or \"Tails\".";
		}
		return helpMessage;
	}
}
