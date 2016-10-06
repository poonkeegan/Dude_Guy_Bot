package com.keegan.bot.Dude_Guy_Bot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;

public class BotLeague extends Command {
	static final String API_URL = "https://na.api.pvp.net/api/lol/";
	static final String REGION = "na/";
	static final String REGNUM = "NA1/";
	static final String SUMMONER_LOOKUP = "v1.4/summoner/by-name/";
	static final String SUMMONER_LEAGUE_LOOKUP = "v2.5/league/by-summoner/";
	static final String STAT_CALL = "v1.3/stats/by-summoner/";
	static String SEC_OBS = "observer-mode/rest/consumer/getSpectatorGameInfo/";
	static final String API_KEY = "?api_key=2d6c0670-5a5a-46b9-a6b0-97a2ad236325";
	static HashMap<String, String> game_modes;


	public void run() {

		String[] args = getArgs();

		if (args.length == 0) {
			displayMessage("No commands passed, please try again. Type \"#league help\" if you need help with this command.");
		// Retrieve champion data
		} else if (args[0].equals("champion")) {
			
		// Retrieve summoner data of the current season
		} else if (args[0].equals("summoner")) {

			try {
				// Get Summoner ID
				long summonerID = getSummonerID(args[1]);
				// Summoner lookup failed
				if (summonerID == -1) {
					displayMessage("Summoner not found.");
				} else {
					String summonerSummary = "```\n";
					// Retrieve Summoner's name and level
					JSONObject summonerBasicsJSON = getJSON(API_URL + REGION + "v1.4/summoner/" + summonerID + API_KEY).getJSONObject(new Long(summonerID).toString());

					// Name
					summonerSummary += summonerBasicsJSON.getString("name") + "\n";

					// Level
					summonerSummary += "Level: " + summonerBasicsJSON.getInt("summonerLevel") + "\n\n\n";

					// Retrieve Summoner's stats
					JSONArray summonerStatsJSON = getJSON(API_URL + REGION + STAT_CALL + summonerID + "/summary" + API_KEY).getJSONArray("playerStatSummaries");

					// Ranked gameplay information
					//summonerSummary += "Ranked Solo Queue 5v5:\n";
					JSONObject rankedLeagueJSON = getJSON(API_URL + REGION + SUMMONER_LEAGUE_LOOKUP + summonerID + "/entry" + API_KEY).getJSONArray(new Long(summonerID).toString()).getJSONObject(0);
					//JSONObject rankedStatsJSON = summonerStatsJSON.getJSONObject(summonerStatsJSON.length-2);
					summonerSummary += (rankedLeagueJSON.getString("tier").charAt(0) + rankedLeagueJSON.getString("tier").substring(1).toLowerCase()) + " " + rankedLeagueJSON.getJSONArray("entries").getJSONObject(0).getString("division") + "\n"; // Summoner league tier + division
					summonerSummary += rankedLeagueJSON.getString("name") + "\n"; // Summoner league name
					summonerSummary += rankedLeagueJSON.getJSONArray("entries").getJSONObject(0).getInt("leaguePoints") + " LP\n\n"; // LP
					summonerSummary += rankedLeagueJSON.getJSONArray("entries").getJSONObject(0).getInt("wins") + " wins\n"; // Ranked wins
					summonerSummary += rankedLeagueJSON.getJSONArray("entries").getJSONObject(0).getInt("losses") + " losses\n\n\n"; // Ranked losses

					// Unranked Stats
					summonerSummary += "Stats (Unranked):\n";
					JSONObject unrankedStatsJSON = summonerStatsJSON.getJSONObject(summonerStatsJSON.length()-1);
					summonerSummary += "Wins: " + unrankedStatsJSON.getInt("wins") + "\n"; // Wins
					summonerSummary += "Kills: " + unrankedStatsJSON.getJSONObject("aggregatedStats").getInt("totalChampionKills") + "\n"; // Summoner takedowns
					summonerSummary += "Assists: " + unrankedStatsJSON.getJSONObject("aggregatedStats").getInt("totalAssists") + "\n"; // Summoner takedown assists
					summonerSummary += "Turrets Destroyed: " + unrankedStatsJSON.getJSONObject("aggregatedStats").getInt("totalTurretsKilled") + "\n"; // Turrets destroyed
					summonerSummary += "CS: " + (unrankedStatsJSON.getJSONObject("aggregatedStats").getInt("totalMinionKills") + unrankedStatsJSON.getJSONObject("aggregatedStats").getInt("totalNeutralMinionsKilled")) + "\n\n\n"; // Creep Score

					// ARAM Stats
					summonerSummary += "Stats (ARAM):\n";
					JSONObject aramStatsJSON = summonerStatsJSON.getJSONObject(summonerStatsJSON.length()-5);
					summonerSummary += "Wins: " + aramStatsJSON.getInt("wins") + "\n"; // Wins
					summonerSummary += "Kills: " + aramStatsJSON.getJSONObject("aggregatedStats").getInt("totalChampionKills") + "\n"; // Summoner takedowns
					summonerSummary += "Assists: " + aramStatsJSON.getJSONObject("aggregatedStats").getInt("totalAssists") + "\n"; // Summoner takedown assists
					summonerSummary += "Turrets Destroyed: " + aramStatsJSON.getJSONObject("aggregatedStats").getInt("totalTurretsKilled"); // Turrets destroyed

					// Output
					displayMessage(summonerSummary + "\n```");
				}

			} catch(ArrayIndexOutOfBoundsException e) {
				displayMessage("No summoner name given. Please try again");
			} catch (Exception e) {
				displayError(e);
			}

		// Get game info
		} else if (args[0].equals("game")) {
			try {
				String buffer = args[1] + " has been playing ";
				// Fetch ID of summoner
				long summonerID = getSummonerID(args[1]);
				if (summonerID == -1) {
					displayMessage("Summoner not found.");
				} else {
					// Fetch the game summoner is in
					JSONObject game_vars = getJSON(API_URL + SEC_OBS + REGNUM + summonerID + API_KEY);
					// Get game type
					buffer += game_vars.getString("gameMode");
					// Get game length
					displayMessage(buffer);
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				displayMessage("No Summoner name given, please try again");
			} catch (Exception e) {
				displayError(e);
			}
		}
		
	}

	/**
	 * Returns the JSONObject of the data retrieved from the specified url
	 */
	private JSONObject getJSON(String url) {
		JSONTokener urlStream = null;
		try {
			URL page = new URL(url);
			try {
				urlStream = new JSONTokener(page.openStream());
			} catch (JSONException | IOException e) {
				displayError(e);
			}
		} catch (MalformedURLException e) {
			System.out.println("Bad url");
			displayError(e);
		}
		return new JSONObject(urlStream);
	}
	
	private boolean valid_request(JSONObject requested) {
		boolean valid = !requested.has("status");
/*		if(!valid){
			JSONObject status = requested.getJSONObject("status");
			int status_code = status.getInt("status_code");
			displayMessage("Error: " + status_code);
			displayMessage("Something went wrong please try again");
		}*/
		return valid;
	}
	
	/**
	 * Searches for and returns the id of a summoner in 
	 * the NA region, or -1 if the search failed.
	 */
	private long getSummonerID(String summonerName) {
		long summonerID = -1;
		// Attempt to access information of the summoner, where their ID is stored
		JSONObject summonerJSON = getJSON(API_URL + REGION + SUMMONER_LOOKUP + summonerName.toLowerCase() + API_KEY);
		// Check that the page is valid and does not return an error code
		if (!valid_request(summonerJSON)) {
			JSONObject httpStatus = summonerJSON.getJSONObject("status");
			int statusCode = httpStatus.getInt("status_code");
			if (statusCode == 404) {
				displayMessage("The summoner " + summonerName + " was not found.");
			// Either api request limit exceeded (429), internal server error (500), or service unavailable (503)
			} else if (statusCode == 429 || statusCode == 500 || statusCode == 503) {
				displayMessage("Summoner lookup is currently unavailable. Please try again later.");
			} else {
				displayMessage("Something went wrong! Please try again");
				displayMessage("Error: " + statusCode);
			}
		} else {
			summonerID = summonerJSON.getJSONObject(summonerName.toLowerCase()).getLong("id");
		}

		return summonerID;
	}

	/**
	 * Return the command-specific help String to BotHelp
	 */
	public String getHelp() {
		String helpMessage = "List of parameters for the 'league' command:\n\n";
		helpMessage += "'champion *champion_name*'\nRetrieves the specified champion's data\n\n";
		helpMessage += "'summoner *summoner_name*'\nRetrieves the specified summoner's data\n\n";
		helpMessage += "'game *summoner_name*'\nRetrieves data of the game the specified summoner is currently in";
		return helpMessage;
	}
}
