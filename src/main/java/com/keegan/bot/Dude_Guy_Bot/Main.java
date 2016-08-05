package com.keegan.bot.Dude_Guy_Bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import sx.blah.discord.util.DiscordException;

public class Main {

	private static HashMap<String, String> api_keys;
    public static void main(String[] args) {
    	api_keys = new HashMap<String, String>();
        Instance bot;
        if (args.length < 2) {
            throw new IllegalArgumentException("Please enter a token and key as an argument");
        } else {
        	System.out.println("Starting bot with token");
            bot = new Instance(args[0], args[1]);
            System.out.println(args[0] + " " + args[1]);
        }
        try {
    		File key_file = new File("secret_key.txt");
    		try{
    			Scanner sc = new Scanner(key_file);
    			while (sc.hasNextLine()) {
    				assignKeys(sc.nextLine());
    			}
    			sc.close();
    		}catch(FileNotFoundException e){
    			System.out.println("No api keys found.");
    		}
            bot.login();
        } catch (DiscordException e) {
            System.out.println("Bot could not start" + e);
        }
    }
    
    private static void assignKeys(String to_assign){
		/**
		 * Mutator for HashMap
		 * Takes in a white space split key/value
		 * pair and stores them in the bot
		 */
		String[] key_val = to_assign.split(" ");
		api_keys.put(key_val[0], key_val[1]);
	}
	
	public static String getApiKey(String api){
		return api_keys.get(api);
	}
	
	public static String readableTime(int time){
		/**
		 * Returns a human readable length of time
		 * given an input of time in seconds.
		 * REQ: time >= 0
		 */
		
		String [] time_units = {"s", "m", "h"};
		String readable_time = "";
		for(int i = 0; i < 3; i++){
			int curr_time = time % 60;
			if (curr_time > 0){
				readable_time = curr_time + time_units[i] + readable_time;
			}
			time /= 60;
		}
		return readable_time;
	}
	
	
}
