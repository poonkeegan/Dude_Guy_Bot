package com.keegan.bot.Dude_Guy_Bot;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;


public class BotKanColle extends Command {

	private static final String API_URL = "http://kancolle.wikia.com/api/v1";
	private static final int WIKIA_ID = 813333;
	private static String api_key;
	private final JSONObject SHIP_DATA = getJson("https://raw.githubusercontent.com/prototype-A/KCTools/master/Lib/Data/ShipData.json");
	private final JSONObject EQUIP_DATA = getJson("https://raw.githubusercontent.com/prototype-A/KCTools/master/Lib/Data/EquipmentData.json");

	/*
	public void init(IMessage m, IDiscordClient b){
		super.init(m, b);
		api_key = "?api_key=" + Main.getApiKey("kc");
		
	}*/

	public void run() {

		String[] args = getArgs();

		if (args.length == 0) {
			displayMessage("No command passed, please try again.");
		/** 
		 * Get ship data (stats)
		 */
		} else if (args[0].equals("kanmusu")) {
			try {
				String shipName = Character.toUpperCase(args[1].charAt(0)) + args[1].substring(1); // Capitalized first keyword
				String remodelModifier = ""; // "kai", "kai ni", "kai ni a", etc.
				// Remodel modifiers
				for (int i = 2; i < args.length; i++) {
					// Not reversible third remodel character ("A", "B", "D", etc.)
					if (args[i].length() > 1) {
						remodelModifier += " " + Character.toUpperCase(args[i].charAt(0)) + args[i].substring(1); // Capitalize
					}
					else {
						remodelModifier += " " + Character.toUpperCase(args[i].charAt(0)); // Capitalize the character
					}
				}
				// Submarine Nicknames
				// U-511 ("Yuu")
				if (shipName.toLowerCase().equals("yuu") || (shipName + remodelModifier).toLowerCase().equals("yuu-chan")) {
					shipName = "U-511";
					remodelModifier = "";
				// Ro-500 ("Ro")
				} else if (shipName.toLowerCase().equals("ro") || shipName.toLowerCase().equals("ro-chan")) {
					shipName = "Ro-500";
					remodelModifier = "";
				// I-8 ("Hachi")
				} else if (shipName.toLowerCase().equals("hachi") || (shipName + remodelModifier).toLowerCase().equals("hacchan")) {
					shipName = "I-8";
					remodelModifier = "";
				// I-19 ("Iku")
				} else if (shipName.toLowerCase().equals("iku")) {
					shipName = "I-19";
					remodelModifier = "";
				// I-26 ("Nimu")
				} else if (shipName.toLowerCase().equals("nimu")) {
					shipName = "I-26";
					remodelModifier = "";
				// I-58 ("Goya")
				} else if (shipName.toLowerCase().equals("goya")) {
					shipName = "I-58";
					remodelModifier = "";
				// I-168 ("Imuya")
				} else if (shipName.toLowerCase().equals("imuya")) {
					shipName = "I-168";
					remodelModifier = "";
				// I-401 ("Shioi")
				} else if (shipName.toLowerCase().equals("shioi")) {
					shipName = "I-401";
					remodelModifier = "";
				}
				try {
					// This is the format
					/**
					 * Name (Hiragana) or SubName (SubNickName)
					 * ShipClass shipType "Number" ShipNumber
					 *
					 * "Classification: " ClassAbbreviation
					 *
					 * "Artist: " Artist
					 * "Seiyuu: " Seiyuu
					 * 
					 * 
					 * "Build Time: " BuildTime "(LSC)"
					 * 
					 * "Next Remodel: " RemodelName " at Level " RemodelLvl (BlueprintRequired)
					 * 
					 * 
					 * "Stats:"
					 * "HP:       " HP (Mar HP)       "FP:   " FP (Max FP)
					 * "Armor:    " AR (Max AR)       "TP:   " TP (Max TP)
					 * "Evasion:  " EV (Max EV)       "AA:   " AA (Max AA)
					 * "Aircraft: " #Aircraft         "ASW:  " ASW (Max ASW)
					 * "Speed:    " shipSpeed         "LOS:  " LOS (Max LOS)
					 * "Range:    " shipRange         "Luck: " Luck (Max LucK)
					 * 
					 * 
					 * "Equipment:"
					 * Slot 1 (Plane capa.)
					 * Slot 2 (Plane capa.)
					 * Slot 3 (Plane capa.)
					 * Slot 4 (Plane capa.)
					 */
					displayMessage("```" + getShipData(shipName, remodelModifier.trim(), "NameAndClass") + getArtistAndSeiyuu(shipName) + getShipData(shipName, remodelModifier.trim(), "Stats") + "```");
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
					displayMessage(shipName + remodelModifier + " data does not exist (yet?)");
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				displayMessage("No shipgirl name entered!");
			}
		/** 
		 * Get equipment data (stats)
		 */
		} else if (args[0].equals("equip") || args[0].equals("equipment")) {
			try {
				String equipName = "";
				// Capitalize all words
				for (int i = 1; i < args.length; i++) {
					equipName += " " + Character.toUpperCase(args[i].charAt(0)) + args[i].substring(1);
				}
				try {
					// This is the format
					/**
					 * JapaneseName
					 * Hiragana
					 *
					 * "Rarity:" equipRarity★
					 *
					 *
					 * (SpecialEffect)
					 * 
					 *
					 * "Stats:"
					 * "FP:       "
					 * "Bombing:  "
					 * "TP:       "
					 * "AA:       "
					 * "ASW:      "
					 * "Accuracy: "
					 * "Evasion:  "
					 * "LoS:      "
					 * "Range:    "
					 * 
					 * 
					 * "Improvements:"
					 * improvementShips respectiveImprovementDays
					 * "Materials Needed: " 
					 * "1-5★" numScrews/numScrewsHigherSuccess numDevmats/numDevmatsHigherSuccess (Equipment Needed)
					 * "6-9★" numScrews/numScrewsHigherSuccess numDevmats/numDevmatsHigherSuccess (Equipment Needed)
					 * " 10★" numScrews/numScrewsHigherSuccess numDevmats/numDevmatsHigherSuccess (Equipment Needed)
					 */
					displayMessage("```" + getEquipData(equipName.trim()) + "```");
				} catch (Exception e) {
					/**
					// Iterate over all equipments to find the first equipment that contains the keywords in its name
					try {
						String keywords[] = new String [args.length];
						JSONArray equipJSON = EQUIP_DATA.toJSONArray();
						boolean itemMatchFound = true;
						for (int keyword = 0; keyword < args.length; keyword++) {
							keywords[keyword] = args[keyword];
						}
						for (int item = 0; item < EQUIP_DATA.length(); item++) {
							for (int word = 0; word < keywords.length; word++) {
								if (equipJSON[item].getString("_name").contains(keywords[word])) {
									itemMatchFound = true;
								} else {
									itemMatchFound = false;
								}
							}
							if (itemMatchFound) {
								equipName = equipJSON.getString("_name");
								displayMessage("```" + getEquipData(equipName.trim()) + "```");
							}
						}
					} catch (Exception ex) {
						System.out.println("Error: " + e.getMessage());
						displayMessage(equipName + " data does not exist (yet?)");
					}*/
					System.out.println("Error: " + e.getMessage());
					displayMessage(equipName + " data does not exist (yet?)");
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				displayMessage("No equipment name entered!");
			}
		/** 
		 * Get item info
		 */
		} else if (args[0].equals("item")) {
			displayMessage("```Not yet implemented```");
		// Redirect to 'kc kanmusu' command
		} else if (args[0].equals("ship") || args[0].equals("shipinfo") || args[0].equals("shipdata")) {
			displayMessage("Did you mean *kanmusu*?");
		}

	}

	/**
	 * Returns the JsonObject from url
	 */
	private JSONObject getJson(String webUrl) {
		JSONTokener urlStream = null;
		try {
			URL url = new URL(webUrl);
			try {
				urlStream = new JSONTokener(url.openStream());
			} catch (IOException e) {
				System.out.println("IOException occurred when obtaining JSON");
			}
		} catch (MalformedURLException e) {
			System.out.println("Bad url");
		}
		return new JSONObject(urlStream);
	}

	/**
	 * Searches for the article ID on the wikia that matches the shipgirl searchTerm
	 */
	private int getShipgirlArticleID(String searchTerm) {
		JSONObject jsonObj = getJson(API_URL + "/Search/List?query=" + searchTerm + "&limit=3");
		String shipName = Character.toUpperCase(searchTerm.charAt(0)) + searchTerm.substring(1).toLowerCase();
		try {
			// Check if articles were found
			JSONArray searchResults = jsonObj.getJSONArray("items");
			//System.out.println("Found search results");
			// Return the article id of searchTerm if found
			//System.out.println("Found shipgirl article id: " + searchResults.getJSONObject(0).getInt("id"));
			return searchResults.getJSONObject(0).getInt("id");
		} catch (JSONException e) {
			System.out.println("JSON Error");
		} catch (Exception e) {
			System.out.println("Unspecified Error");	
		}
		return -1;
	}

	/**
	 * Gets the shipgirl's CG artist and voice actress
	 */
	private String getArtistAndSeiyuu(String shipGirl) throws JSONException {
		String artistAndSeiyuu = "";
		if (getShipgirlArticleID(shipGirl) == -1) {
			System.out.println("An article of " + shipGirl + " was not found!");
		} else {
			int articleId = getShipgirlArticleID(shipGirl);
			JSONObject jsonObj = getJson(API_URL + "/Articles/AsSimpleJson?id=" + articleId);
			// JSON "Sections"
			JSONArray sections = jsonObj.getJSONArray("sections");
			try {
				JSONArray appearance = sections.getJSONObject(2).getJSONArray("content").getJSONObject(0).getJSONArray("elements");
				JSONArray personality = sections.getJSONObject(3).getJSONArray("content").getJSONObject(0).getJSONArray("elements");

				// Artist
				try {
		 			artistAndSeiyuu += appearance.getJSONObject(0).getString("text") + "\n";
				} catch (Exception e) {
					artistAndSeiyuu += "Artist: (UNKNOWN)\n";
				}
				// VA
				try {
		 			artistAndSeiyuu += personality.getJSONObject(0).getString("text") + "\n\n\n";
				} catch (Exception e) {
					artistAndSeiyuu += "Seiyuu: (UNKNOWN)\n\n\n";
				}
			// Some "sections" might not exist for newly-implemented kanmusu
			} catch (Exception e) {
				artistAndSeiyuu += "Artist: (UNKNOWN)\n";
				artistAndSeiyuu += "Seiyuu: (UNKNOWN)\n\n\n";
			}
		}
		return artistAndSeiyuu;
	}

	/**
	 * Gets the kanmusu's name, VA, artist and stats
	 */
	private String getShipData(String shipName, String remodelModifiers, String dataType) throws JSONException {
		//String remodelName = null;
		if (dataType.equals("Stats")) {
			System.out.println("Base Ship Name: \"" + shipName + "\"");
			System.out.println("Remodel Modifier: \"" + remodelModifiers + "\"");
			System.out.println("Full Ship Name: \"" + shipName + " " + remodelModifiers + "\"");
		}
		JSONObject shipJSON = SHIP_DATA.getJSONObject(shipName);
		JSONObject statJSON = shipJSON.getJSONObject(remodelModifiers);
		/**
		// If "Kai", check if the remodel name is different from the base ship name (e.g. Taigei, Littorio, etc.)
		if (remodelModifiers.equals("Kai")) {
			try {
				remodelName = shipJSON.getJSONObject("").getString("_remodel_to");
				remodelName = remodelName.substring(0, remodelName.indexOf("/"));
				if (!(remodelName.equals(shipName))) {
					shipJSON = SHIP_DATA.getJSONObject(remodelName);
					statJSON = shipJSON.getJSONObject("");
				}
			} catch (Exception e) {
				System.out.println("JSON for " + remodelName + " was not found.");
			}
		}
		System.out.println(remodelName);
		*/

		// Ship Type Data
		String[][] shipType = new String[22][2];
		shipType[2-1][0] = "Destroyer";
		shipType[2-1][1] = "(DD)";
		shipType[3-1][0] = "Light Cruiser";
		shipType[3-1][1] = "(CL)";
		shipType[4-1][0] = "Torpedo Cruiser";
		shipType[4-1][1] = "(CLT)";
		shipType[5-1][0] = "Heavy Cruiser";
		shipType[5-1][1] = "(CA)";
		shipType[6-1][0] = "Aviation Cruiser";
		shipType[6-1][1] = "(CAV)";
		shipType[7-1][0] = "Light Carrier";
		shipType[7-1][1] = "(CVL)";
		shipType[8-1][0] = "Fast Battleship";
		shipType[8-1][1] = "(FBB)";
		shipType[9-1][0] = "Battleship";
		shipType[9-1][1] = "(BB)";
		shipType[10-1][0] = "Aviation Battleship";
		shipType[10-1][1] = "(BBV)";
		shipType[11-1][0] = "Standard Carrier";
		shipType[11-1][1] = "(CV)";
		shipType[13-1][0] = "Submarine";
		shipType[13-1][1] = "(SS)";
		shipType[14-1][0] = "Aircraft Carrying Submarine";
		shipType[14-1][1] = "(SSV)";
		shipType[16-1][0] = "Seaplane Tender";
		shipType[16-1][1] = "(AV)";
		shipType[17-1][0] = "Amphibious Assault Ship";
		shipType[17-1][1] = "(LHA)";
		shipType[18-1][0] = "Armored Carrier";
		shipType[18-1][1] = "(CVB)";
		shipType[19-1][0] = "Repair Ship";
		shipType[19-1][1] = "(AR)";
		shipType[20-1][0] = "Submarine Tender";
		shipType[20-1][1] = "(AS)";
		shipType[21-1][0] = "Training Cruiser";
		shipType[21-1][1] = "(CT)";
		shipType[22-1][0] = "Fleet Oiler";
		shipType[22-1][1] = "(AO)";

		// Ship Range
		String[] shipRange = {"Short", "Medium", "Long", "Very Long"};

		// Ship Speed
		String[] shipSpeed = new String[10];
		shipSpeed[5-1] = "Slow";
		shipSpeed[10-1] = "Fast";

		String shipData = "";

		/**
		 * Get Ship Name and Class
		 */
		if (dataType.equals("NameAndClass")) {
			// Submarines have nicknames
			if (shipType[statJSON.getInt("_type")-1][1].equals("(SS)")) {
				shipData += statJSON.getString("_japanese_name"); // JP Name
				shipData += " (" + statJSON.getString("_nick") + " " + statJSON.getString("_japanese_nick") + ")\n"; // Nickname
				shipData += statJSON.getString("_class") + " Class "; // Class
				shipData += shipType[statJSON.getInt("_type")-1][0]; // Type Classification
				shipData += " #" + statJSON.getInt("_class_number") + "\n\n"; // Ship Number in class
				shipData += "Classification: " + shipType[statJSON.getInt("_type")-1][1] + "\n\n"; // Abbreviated classification
			// Not a submarine
			} else {
				shipData += statJSON.getString("_japanese_name"); // JP Name
				shipData += " (" + statJSON.getString("_reading") + ")\n"; // Name pronounciation
				shipData += statJSON.getString("_class") + " Class "; // Class
				shipData += shipType[statJSON.getInt("_type")-1][0]; // Type Classification
				shipData += " #" + statJSON.getInt("_class_number") + "\n\n"; // Ship Number in class
				shipData += "Classification: " + shipType[statJSON.getInt("_type")-1][1] + "\n\n"; // Abbreviated classification
			}
		
		/**
		 * Get Ship Stats
		 */
		} else if (dataType.equals("Stats")) {
			// Build Time (Only for base ships)
			try {
				// Unbuildable Ship
				if (!(statJSON.getBoolean("_buildable")) && !(statJSON.getBoolean("_buildable_lsc"))) {
					shipData += "Unbuildable\n\n";
				// Buildable ship
				} else {
					int buildTimeTotalMins = statJSON.getInt("_build_time");
					int buildTimeHours = new Double(Math.floor(buildTimeTotalMins/60)).intValue();
					int buildTimeMins = (buildTimeTotalMins - buildTimeHours*60);
					String buildMins = Integer.toString(buildTimeMins);
					if (buildTimeMins == 0) {
						buildMins = "00";
					}
					shipData += "Build Time: " + buildTimeHours + ":" + buildMins + ":00"; // Build Time
					// Normal construction Ship
					if (!(statJSON.getBoolean("_buildable_lsc"))) {
						shipData += " (LSC)\n\n\n";
					// LSC and normal construction Ship
					} else if (statJSON.getBoolean("_buildable_lsc")) {
						shipData += " (Normal/LSC)\n\n\n";
					} else {
						shipData += " (Normal)\n\n\n";
					}
				}
			// Do nothing for now
			} catch (Exception e) {}

			// Next remodel + requirements
			try {
				JSONObject remodelJSON = shipJSON.getJSONObject(statJSON.getString("_remodel_to").substring(statJSON.getString("_remodel_to").indexOf('/')+1));
				shipData += "Next Remodel: " + statJSON.getString("_remodel_to").replace('/', ' ') + " at Level " + remodelJSON.getInt("_remodel_level");
				// Both blueprint and prototype catapult needed
				if (remodelJSON.getBoolean("_remodel_blueprint") && remodelJSON.getBoolean("_remodel_catapult")) {
					shipData += " (Blueprint & Catapult Required)\n\n\n";
				// Only blueprint needed
				} else if (remodelJSON.getBoolean("_remodel_blueprint")) {
					shipData += " (Blueprint Required)\n\n\n";
				} else {
					shipData += "\n\n\n";
				}
			// No further remodels
			} catch (Exception e) {
				shipData += "Next Remodel: None\n\n\n";
			}

			// Stats 
			shipData += "Stats:\n";

			// HP (Married HP)
			String hpStringLengthTest = "HP:       " + statJSON.getInt("_hp") + " (" + statJSON.getInt("_hp_max") + ")";
			// 1-digit number for HP, 2-digit number for married HP
			if (hpStringLengthTest.length() == 16) {
				shipData += hpStringLengthTest + "        ";
			// 2-digit number for HP, 3-digit number for Married HP
			} else if (hpStringLengthTest.length() == 18) {
				shipData += hpStringLengthTest + "      ";
			} else {
				shipData += hpStringLengthTest + "       ";
			}
			
			// Firepower (Max Firepower)
			// 1-digit FP only
			if ((statJSON.getInt("_firepower")/10) < 1) {
				shipData += "FP:    " + statJSON.getInt("_firepower") + " (" + statJSON.getInt("_firepower_max") + ")\n";
			} else {
				shipData += "FP:   " + statJSON.getInt("_firepower") + " (" + statJSON.getInt("_firepower_max") + ")\n";
			}

			// Armor (Max Armor)
			// 1-digit Armor only
			if ((statJSON.getInt("_armor")/10) < 1) {
				shipData += "Armor:     " + statJSON.getInt("_armor") + " (" + statJSON.getInt("_armor_max") + ")";
			} else {
				shipData += "Armor:    " + statJSON.getInt("_armor") + " (" + statJSON.getInt("_armor_max") + ")";
			}
			String armorStringLengthTest = statJSON.getInt("_armor") + " (" + statJSON.getInt("_armor_max") + ")";
			// 1,2-digit number for Armor, 2-digit number for Max Armor
			if (armorStringLengthTest.length() != 8) {
				shipData += "       ";
			// 2-digit number for Armor, 3-digit number for Max Armor
			} else {
				shipData += "      ";
			}

			// Torpedo Power (Max Torpedo Power)
			// 1-digit TP only
			if ((statJSON.getInt("_torpedo")/10) < 1) {
				shipData += "TP:    " + statJSON.getInt("_torpedo");
			} else {
				shipData += "TP:   " + statJSON.getInt("_torpedo");
			}
			try {
				shipData += " (" + statJSON.getInt("_torpedo_max") + ")\n";
			// Incapable of launching torpedoes
			} catch (Exception e) {
				shipData += "\n";
			}

			// Evasion (Max Evasion)
			String evasion = new Integer(statJSON.getInt("_evasion")).toString();
			if (statJSON.getInt("_evasion") == -1) {
				evasion = "??";
			}
			String maxEvasion = new Integer(statJSON.getInt("_evasion_max")).toString();
			if (statJSON.getInt("_evasion_max") == -1) {
				maxEvasion = "??";
			}
			shipData += "Evasion:  " + evasion + " (" + maxEvasion + ")       ";

			// AA (Max AA)
			// 1-digit AA only
			if ((statJSON.getInt("_aa")/10) < 1) {
				shipData += "AA:    " + statJSON.getInt("_aa");
			} else {
				shipData += "AA:   " + statJSON.getInt("_aa");
			}
			try {
				shipData += " (" + statJSON.getInt("_aa_max") + ")\n";
			// Incapable of AA (Submarines)
			} catch (Exception e) {
				shipData += "\n";
			}

			// Aircraft
			int totalPlaneSlots = countTotalPlaneSlots(statJSON.getJSONArray("_equipment"));
			// 1 digit only
			if ((totalPlaneSlots/10) < 1) {
				shipData += "Aircraft:  " + totalPlaneSlots + "            ";
			} else {
				shipData += "Aircraft: " + totalPlaneSlots + "           ";
			}

			// ASW (Max ASW)
			shipData += "ASW:  ";
			try {
				String asw = Integer.toString((statJSON.getInt("_asw")));
				// Unknown ASW (at the moment)
				if (statJSON.getInt("_asw") == -1) {
					asw = "??";
				// 1-digit ASW only
				} else if ((statJSON.getInt("_asw")/10) < 1) {
					shipData += " " + asw;
				} else {
					shipData += asw;
				}
				String maxAsw = Integer.toString((statJSON.getInt("_asw_max")));
				if (statJSON.getInt("_asw_max") == -1) {
					maxAsw = "??";
				}
				shipData += " (" + maxAsw + ")\n";
			// Incapable of ASW
			} catch (Exception e) {
				shipData += "\n";
			}

			// Speed
			shipData += "Speed:    " + shipSpeed[statJSON.getInt("_speed")-1] + "          ";

			// LOS (Max LoS)
			// 1 digit only
			if ((statJSON.getInt("_los") != -1) && ((statJSON.getInt("_los")/10) < 1)) {
				shipData += "LoS:   ";
			} else {
				shipData += "LoS:  ";
			}
			String los = new Integer(statJSON.getInt("_los")).toString();
			if (statJSON.getInt("_los") == -1) {
				los = "??";
			}
			String maxLos = new Integer(statJSON.getInt("_los_max")).toString();
			if (statJSON.getInt("_los_max") == -1) {
				maxLos = "??";
			}
			shipData += los + " (" + maxLos + ")\n";

			// Range
			shipData += "Range:    " + shipRange[statJSON.getInt("_range")-1];
			if (shipRange[statJSON.getInt("_range")-1].equals("Very Long")) {
				shipData += "     ";
			} else if (shipRange[statJSON.getInt("_range")-1].equals("Long")) {
				shipData += "          ";
			} else if (shipRange[statJSON.getInt("_range")-1].equals("Medium")) {
				shipData += "        ";
			} else {
				shipData += "         ";
			}

			// Luck (Max Luck)
			shipData += "Luck: " + statJSON.getInt("_luck") + " (" + statJSON.getInt("_luck_max") + ")\n\n\n";
		
			// Equipment Slots + Equipment
			shipData += "Equipment:\n";
			for (int equip = 0; equip < statJSON.getJSONArray("_equipment").length(); equip++) {
				// Equipped equipment
				try {
					shipData += "Slot " + (equip + 1) + ": " + statJSON.getJSONArray("_equipment").getJSONObject(equip).getString("equipment") + " (" + statJSON.getJSONArray("_equipment").getJSONObject(equip).getInt("size") + " plane space(s))\n";
				// Empty slot
				} catch (Exception e) {
					shipData += "Slot " + (equip + 1) + ":  - Unequipped -   (" + statJSON.getJSONArray("_equipment").getJSONObject(equip).getInt("size") + " plane space(s))\n";
				}
			}
		}
		return shipData;
	}

	/**
	 * Count the total number of (any) planes that the ship can hold
	 */
	private int countTotalPlaneSlots(JSONArray equipmentJSON) {
		int totalPlaneSlots = 0;
		for (int slot = 0; slot < equipmentJSON.length(); slot++) {
			totalPlaneSlots += equipmentJSON.getJSONObject(slot).getInt("size");
		}
		return totalPlaneSlots;
	}

	/**
	 * Gets the equipment's name, stats and Akashi improvement info
	 */
	private String getEquipData(String equipName) {

		JSONObject equipJSON = EQUIP_DATA.getJSONObject(equipName);
		String equipData = "";

		// Equipment (Firing) Range
		String[] equipRange = {"Short", "Medium", "Long", "Very Long"};

		// JP Name
		equipData += equipJSON.getString("_japanese_name") + "\n";
		// JP Name Pronouciation
		equipData += equipJSON.getString("_reading") + "\n\n";

		// Rarity in ★s
		equipData += "Rarity: " + new String(new char[equipJSON.getInt("_rarity")+1]).replace("\0", "★") + "\n\n\n";

		// Special Effect (if any)
		try {
			equipData += equipJSON.getString("_special")+ "\n\n\n";
	 	} catch (Exception e) {
			// Don't show it if it doesn't have a special effect
		}

		equipData += "Stats: \n";

		// Bombing
		try {
			// 1-digit Bombing stat
			if (equipJSON.getInt("_bombing")/10 < 1) {
				equipData += "Bombing:  " + equipJSON.getInt("_bombing") + "\n";
			// 2-digit Bombing stat
			} else {
				equipData += "Bombing: " + equipJSON.getInt("_bombing") + "\n";
			}
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// Firepower
		try {
			// 1-digit FP stat
			if (equipJSON.getInt("_firepower")/10 < 1) {
				equipData += "FP:       " + equipJSON.getInt("_firepower") + "\n";
			// 2-digit FP stat
			} else {
				equipData += "FP:      " + equipJSON.getInt("_firepower") + "\n";
			}
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// Torpedo Power
		try {
			// 1-digit FP stat
			if (equipJSON.getInt("_torpedo")/10 < 1) {
				equipData += "TP:       " + equipJSON.getInt("_torpedo") + "\n";
			} else {
				equipData += "TP:      " + equipJSON.getInt("_torpedo") + "\n";
			}
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// AA
		try {
			// 1-digit AA stat
			if (equipJSON.getInt("_aa")/10 < 1) {
				equipData += "AA:       " + equipJSON.getInt("_aa") + "\n";
			// 2-digit AA stat
			} else {
				equipData += "AA:      " + equipJSON.getInt("_aa") + "\n";
			}
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// ASW
		try {
			// 1-digit ASW stat
			if (equipJSON.getInt("_asw")/10 < 1) {
				equipData += "ASW:      " + equipJSON.getInt("_asw") + "\n";
			// 2-digit ASW stat
			} else {
				equipData += "ASW:     " + equipJSON.getInt("_asw") + "\n";
			}
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// Shelling Accuracy
		try {
			equipData += "Accuracy: " + equipJSON.getInt("_shelling_accuracy") + "\n";
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// Evasion
		try {
			// 1-digit Evasion stat
			if (equipJSON.getInt("_evasion")/10 < 1) {
				equipData += "Evasion:  " + equipJSON.getInt("_evasion") + "\n";
			// 2-digit Evasion stat
			} else {
				equipData += "Evasion:  " + equipJSON.getInt("_evasion") + "\n";
			}
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// LoS
		try {
			// 1-digit LoS stat
			if (equipJSON.getInt("_los")/10 < 1) {
				equipData += "LoS:      " + equipJSON.getInt("_los") + "\n";
			// 2-digit LoS stat
			} else {
				equipData += "LoS:     " + equipJSON.getInt("_los") + "\n";
			}
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// Range
		try {
			equipData += "Range: " + equipRange[equipJSON.getInt("_range")-1] + "\n";
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}

		// Combat Radius (for planes)
		try {
			equipData += "Range: " + equipJSON.getInt("_combat_radius") + "\n";
		} catch (Exception e) {
			// Don't show it if it doesn't add the stat
		}
		
		return equipData;
	}

	/**
	 * Return the command-specific help String to BotHelp
	 */
	public String getHelp() {
		String helpMessage = "List of parameters for the 'kc' command:\n\n";
		helpMessage += "'kanmusu *shipgirl_name*'\nDisplay detailed stats of the shipgirl\n\n";
		helpMessage += "'equip *equipment_name*'\nDisplay detailed stats of the equipment\n\n";
		helpMessage += "'item *item_name*'\nDisplay information on the item";
		return helpMessage;
	}

}
