package com.keegan.Dude_Guy_Bot;

import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

public class BotLeague extends Command{
	static final String API_URL = "https://na.api.pvp.net/";
	static final String REG = "na/";
	static final String REGNUM = "NA1/";
	static String SEC_API = "api/lol/";
	static String SEC_OBS = "observer-mode/rest/consumer/getSpectatorGameInfo/";
	public void run() {
		String[] args = getArgs();
		if(args.length == 0){
			displayMessage("No command extra commands passed, please try again.");
		}else if (args[0].equals("game")){
			
			String api_key = "?api_key=" + Main.getApiKey("league");
			try{
				URL fetch_id = new URL(API_URL + SEC_API + REG + "v1.4/summoner/by-name/" + args[1] + api_key);
				displayMessage(fetch_id.toString());
				JSONTokener id_page = new JSONTokener(fetch_id.openStream());
				JSONObject id_vars = new JSONObject(id_page);
				JSONObject summ_vars = id_vars.getJSONObject(args[1].toLowerCase());
				long summ_id = summ_vars.getLong("id");
				displayMessage(summ_id + "");
				URL fetch_game = new URL(API_URL + SEC_OBS + REGNUM + summ_id + api_key);
				displayMessage(fetch_game.toString());
				JSONTokener game_page = new JSONTokener(fetch_game.openStream());
				JSONObject game_vars = new JSONObject(game_page);
				displayMessage("Break");
				displayMessage(game_vars.getString("gameMode"));
			}catch(ArrayIndexOutOfBoundsException e){
				displayMessage("No Summoner name given, please try again");
			} catch (Exception e) {
				displayError(e);
			}
			
		}
		
		
	}

}
