package com.keegan.Dude_Guy_Bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.util.DiscordException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Instance bot;
        if (args.length < 2) {
            throw new IllegalArgumentException("Please enter a token and key as an argument");
        } else {
        	log.info("Starting bot with token");
            bot = new Instance(args[0], args[1]);
        }
        try {
            bot.login();
        } catch (DiscordException e) {
            log.warn("Bot could not start", e);
        }
    }
}