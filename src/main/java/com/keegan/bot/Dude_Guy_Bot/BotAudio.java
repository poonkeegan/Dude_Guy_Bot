package com.keegan.bot.Dude_Guy_Bot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.json.generic.GameObject;
import sx.blah.discord.util.DiscordException;


public class BotAudio extends Command {

	static final float VOL_CONST = 2.7f/2500;
	static String[][] songRepeatList = new String[5][2];
	static GameObject currentSongPlaying;

	public void run() {
		/**
		 * Implement commands for playing audio
		 */
		// Check for permissions
		if (isAdmin() || isTester()) {
			// Get arguments passed
			String[] args = getArgs();
			File music = null;
			// Handle no parameters

			if(args.length == 0) {
				displayMessage("No parameters passed");
				String toDisp = "Correct usage " + Instance.getKey(message) + Instance.getCmd(message) + " \"parameters\"";
				displayMessage(toDisp);
			}
			else {
				// Initialize audio channel
				AudioChannel audio_chn = loadCurrChn();
				if (args[0].equals("queue")) {
					URL url = processYoutubeURL(args);
					if (url != null) {
						// Queue up the url from YT
						if (args[1].contains("youtube.com")) {
							queueYoutubeSong(audio_chn, music, url);
						} else {
							// Get the audio from the video on specified website to queue
							displayMessage(url.toString());
							audio_chn.queueUrl(url);
						}
						displayMessage(music.getName() + " queued.");
					}
				}
				// Handle pausing music
				else if (args[0].equals("pause")) {
					// Bot must have something queued to play
					if (audio_chn.getQueueSize() == 0){
						displayMessage("There is nothing playing");
					}
					else {
						audio_chn.pause();
						currentSongPlaying = null;
					}
				}

				else if (args[0].equals("play")) {
					if (audio_chn.getQueueSize() == 0) {
						displayMessage("There is nothing to play currently.");
					}
					else {
						audio_chn.resume();
						currentSongPlaying = new GameObject(music.getName());
					}
				}

				else if (args[0].equals("clear")) {
					// Bot must have something queued to clear
					if (audio_chn.getQueueSize() != 0) {
						audio_chn.clearQueue();
						displayMessage("Queue Cleared.");
					} else {
						displayMessage("Nothing to clear");
					}
				}

				else if (args[0].equals("skip")) {
					// Bot must have something queued to play
					if (audio_chn.getQueueSize() == 0) {
						displayMessage("There is nothing to skip currently.");
					}
					else {
						audio_chn.skip();
						currentSongPlaying = null;
						displayMessage("Song Skipped.");
					}
				}

				else if (args[0].equals("volume")) {
					try {
						float volume = Float.parseFloat(args[1]);
						if (volume < 0 || volume > 100) {
							displayMessage(args[1] + " is not within the range [0, 100], please try again");
						}
						else {
							volume *= VOL_CONST;
							volume = (float) Math.pow(10, volume) - 1;
							audio_chn.setVolume(volume);
							displayMessage("Volume set to " + args[1]);
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						displayMessage("You did not input a number to set volume to, please try again");
					} catch(NumberFormatException e) {
						displayMessage(args[1] + " is not a valid number, please try again");
					}
				}
				else if (args[0].equals("repeat")) {
					displayRepeatList();
					try {
						int songNum = Integer.parseInt(args[1]);
						if (songNum < 1 || songNum > 5) {
							displayMessage(args[1] + " is not within the range [1, 5], please try again");
						}
						else {
              				try {
							  queueYoutubeSong(loadCurrChn(), music, new URL(songRepeatList[songNum-1][0]));
							} catch(Exception e) {
                				displayMessage(e.getMessage());
              				}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						displayRepeatList(); // Just display list of last 5 songs played if no number was entered
					} catch (NumberFormatException e) {
						displayMessage(args[1] + " is not a valid number, please try again");
					}
				}
			}
		} else {
			// Someone without bot permissions tried to execute a command
			displayMessage("Insufficient Permissions");
		}

	}

	public void joinChannel() {
		/**
		 * Attempts to have the bot join the voice channel that user is in
		 */
		IVoiceChannel curr_chn = null;
		try {
			// Get channel of sender
			curr_chn = message.getAuthor().getVoiceChannel().get();
			// Connect to sender's channel
			if (!bot.getConnectedVoiceChannels().contains(curr_chn)) {
				curr_chn.join();
			}
		}
		// Make sure that there is a channel to join to
		catch (NoSuchElementException e) {
			displayMessage("You are not currently in a valid channel to perform this command");
		}
	}

	private File loadYoutubeMP3(URL url, AudioChannel audio_chn) {
		/**
		 * Queues a music file from a youtube url
		 */
		File music = null;
		try {
			// Download youtube and convert to mp3 using youtube-dl
			String dl_dir = "youtube-dl -o " + System.getProperty("user.dir");
      		dl_dir += "/%(title)s.%(ext)s -x --audio-format mp3 " + url;
      		Process py = Runtime.getRuntime().exec(dl_dir);
			displayMessage(dl_dir);
      		displayMessage("Downloading File");
			BufferedReader in = new BufferedReader(new InputStreamReader(py.getInputStream()));
			String input;
			String title = null;
			boolean correct_line;
			input = in.readLine();
			py.waitFor();
			displayMessage("Now Loading File");

			// Load the mp3 file into the bot
			File dir = new File(System.getProperty("user.dir"));
			for (File file : dir.listFiles()) {
				if (file.getName().endsWith(".mp3")) {
					music = file;
					currentSongPlaying = new GameObject(music.getName());
				}
			}
		} catch (Exception e) {
			displayMessage(e.getMessage());
		}
		return music;
	}

	private String processYoutubeTitle(String input) {
		/**
		 * Strips the video title out of a download log message
		 */
		int startIndex = "[download] Destination: ".length();
		int endIndex = input.lastIndexOf('.');
		String title = input.substring(startIndex, endIndex);
		return title;
	}

	private URL processYoutubeURL(String[] args) {
		// Initialize the given url
		URL url = null;
		try {
			// Remove extra parameters in the url
			int cutoff = args[1].indexOf('&');
			if (cutoff == -1) {
				cutoff = args[1].length();
			}
			url = new URL(args[1].substring(0, cutoff));
		} catch (ArrayIndexOutOfBoundsException e) {
			displayMessage("No URL provided");
		} catch (MalformedURLException e) {
			displayMessage("Invalid URL");
		}
		return url;
	}

	private AudioChannel loadCurrChn() {
		AudioChannel curr_chn = null;
		try {
			curr_chn = bot.getGuilds().get(0).getAudioChannel();
		} catch(DiscordException e) {
			displayMessage(e.getErrorMessage());
		}
		return curr_chn;
	}

	/**
	 * Helper method to queue Youtube songs
	 */
	private void queueYoutubeSong(AudioChannel audio_chn, File music, URL url) {
		// Get the audio from the YT video to queue
		music = loadYoutubeMP3(url, audio_chn);
		audio_chn.queueFile(music);
		addSongToRepeatList(music.getName(), url.toString();
		displayMessage(music.getName());
		// Delete the downloaded youtube file
		try {
			Files.delete(music.toPath());
		} catch (IOException x) {
			displayMessage(x.getMessage());
		}
	}

	/**
	 * Display the last 5 songs played by the
     * bot for user to choose to repeat
	 */
	private String displayRepeatList() {
		String songList = "";
		try {
			songList += "Last 5 songs played: ";
			for (int song = 0; song < 5; song++) {
				songList += "\n"+(song+1)+". "+songRepeatList[song][0];
			}
		} catch(DiscordException e) {
			displayMessage(e.getErrorMessage());
		}
		return songList;
	}

	/**
	 * Add the current queued song to the array 
     * of unique songs that can be repeated
	 */
	private void addSongToRepeatList(String songName, String songUrl) {
		// Check if song is from Youtube, otherwise don't add it
		if (songUrl.contains("youtube.com")) {
			// Check if array is full
			boolean isFull = true;
			for (int song = 0; song < songRepeatList.length; song++) {
				if (songRepeatList[song][1] == null) {
					// Array is not full: check if current queued song is the same as the last queued song
					isFull = false;
					// If repeat list is not empty
					if (song != 0) {
						// If unique song (different from last): add song to list
						if (!(songRepeatList[song-1][0].equals(songUrl))) {
							for (int i = song; i > 0 ; i--) {
								songRepeatList[i][0] = songRepeatList[i-1][0];
								songRepeatList[i][1] = songRepeatList[i-1][1];
							}
							songRepeatList[0][0] = songName;
							songRepeatList[0][1] = songUrl;
						}
					} else {
						// List is empty: add as first song on list
						songRepeatList[0][0] = songName;
						songRepeatList[0][1] = songUrl;
					}
					break;
				}
			}
			// Array is full: check if unique song
			if (isFull) {
				/**
				 * If unique song: shift songs on list back 1 position, thus 
				 * removing the song played 6 songs ago, and add the newest 
				 * unique song. Otherwise do nothing
				 */
				if (!(songUrl.equals(songRepeatList[0][0]))) {
					for (int i = 4; i > 0; i--) {
						songRepeatList[i][0] = songRepeatList[i-1][0];
						songRepeatList[i][1] = songRepeatList[i-1][1];
					}
					songRepeatList[0][0] = songName;
					songRepeatList[0][1] = songUrl;
				}
			}
		}
	}
}
