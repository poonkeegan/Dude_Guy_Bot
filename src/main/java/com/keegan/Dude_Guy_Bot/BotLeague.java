package com.keegan.Dude_Guy_Bot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;

public class BotLeague extends Command{
	static final String API_URL = "https://na.api.pvp.net/";
	static final String REG = "na/";
	static final String REGNUM = "NA1/";
	static String SEC_API = "api/lol/";
	static String SEC_OBS = "observer-mode/rest/consumer/getSpectatorGameInfo/";
	static String api_key;
	static final long NOT_FOUND = -1;
	static HashMap<String, String> game_modes;
	game_modes.put("CLASSIC", "Classic")
	
	public void init(IMessage m, IDiscordClient b){
		super.init(m, b);
		api_key = "?api_key=" + Main.getApiKey("league");
		
	}
	public void run() {
		String[] args = getArgs();
		if(args.length == 0){
			displayMessage("No command extra commands passed, please try again.");
		}else if (args[0].equals("game")){

			try{
				String buffer = args[1] + " has been playing ";
				// Fetch ID of summoner
				long summ_id = get_summ_id(args[1]);
				// Fetch the game summoner is in
				JSONObject game_vars = get_page_obj(API_URL + SEC_OBS + REGNUM + summ_id + api_key);
				// Get game type
				buffer += game_vars.getString("gameMode");
				// Get game length
				displayMessage(buffer);
			}catch(ArrayIndexOutOfBoundsException e){
				displayMessage("No Summoner name given, please try again");
			} catch (Exception e) {
				displayError(e);
			}
			
		}
		
		
	}
	private JSONObject get_page_obj(String url){
		// Access the given url
		URL page = null;
		try {
			page = new URL(url);
		} catch (MalformedURLException e) {
			displayError(e);
		}
		// Convert the page into a json object
		JSONTokener page_tokener = null;
		try {
			page_tokener = new JSONTokener(page.openStream());
		} catch (JSONException | IOException e) {
			displayError(e);
		}
		JSONObject page_obj = new JSONObject(page_tokener);
		return page_obj;
	}
	
	private boolean valid_request(JSONObject requested){
		boolean valid = !requested.has("status");
/*		if(!valid){
			JSONObject status = requested.getJSONObject("status");
			int status_code = status.getInt("status_code");
			displayMessage("Error: " + status_code);
			displayMessage("Something went wrong please try again");
		}*/
		return valid;
	}
	
	private long get_summ_id(String summ_name){
		/**
		 * Retrieves the id number of a summoner in the NA region
		 * given their player id
		 */
		long summ_id = NOT_FOUND;
		// Change to lower case so as to be consistent with the api handling
		summ_name = summ_name.toLowerCase();
		// Attempt to access information of the summoner, where their ID is stored
		JSONObject id_vars = get_page_obj(API_URL + SEC_API + REG + "v1.4/summoner/by-name/" + summ_name + api_key);
		// Check that the page is valid and does not return an error code
		if (!valid_request(id_vars)){
			JSONObject status = id_vars.getJSONObject("status");
			int status_code = status.getInt("status_code");
			if (status_code == 404){
				displayMessage("The summoner " + summ_name + " was not found.");
			}else {
				displayMessage("Error: " + status_code);
				displayMessage("Something went wrong please try again");
			}
			
		}else {
			JSONObject summ_vars = id_vars.getJSONObject(summ_name);
			summ_id = summ_vars.getLong("id");
		}
		return summ_id;
	}
}
