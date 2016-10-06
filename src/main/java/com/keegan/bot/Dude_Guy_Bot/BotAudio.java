package com.keegan.bot.Dude_Guy_Bot;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.NoSuchElementException;

import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;


public class BotAudio extends Command {

	private static final float VOL_CONST = 2.7f/2500;
	private static String[][] songRepeatList = new String[5][2];
	private static AudioPlayer audioPlayer;
	private static File currentSong = null;
	private static File prevSong = null;

	public BotAudio(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

	public void run() {
		/**
		 * Implement commands for playing audio
		 */
		// Check for permissions
		if (isAdmin() || isTester()) {
			// Get arguments passed
			String[] args = getArgs();
			
			// Handle no parameters
			if (args.length == 0) {
				displayMessage("No parameters passed");
				String toDisp = "Correct usage " + Instance.getKey(message) + Instance.getCmd(message) + " \"parameters\"";
				displayMessage(toDisp);
			}
			else {
				if (args[0].equals("queue")) {
					URL url = processYoutubeURL(args);
					if (url != null) {
						// Queue up the url from YT
						if (args[1].contains("youtube.com")) {
							try {
								currentSong = dlYoutubeMP3(url);
								String songName = currentSong.getName().substring(0, currentSong.getName().length() - 4);
								audioPlayer.queue(audioPlayer.queue(currentSong)); // The inner queue() converts the downloaded YT file into an AudioPlayer.Track object to be queued by the outer queue()
								addSongToRepeatList(songName, url.toString());
								deletePreviousSong();
								displayMessage("**" + songName + "** queued");
							}
							catch (Exception e) {
								System.out.println("An error occured while queuing a song: " + e);
							}
						}
						// Queue up a direct file (.mp3, .ogg, .flac and .wav)
						else if (args[1].contains(".mp3") ||args[1].contains(".ogg") || args[1].contains(".flac") || args[1].contains(".wav")) {
							String songName = currentSong.getName().substring(0, currentSong.getName().length() - 4);
							try {
								audioPlayer.queue(audioPlayer.queue(url));
								addSongToRepeatList(songName, url.toString());
								deletePreviousSong();
								displayMessage("**" + songName + "** queued");
							}
							catch (Exception e) {
								System.out.println("An error occured while queuing a direct file link: " + e);
							}
						}
						// Get the audio from the video on specified website to queue
						else {
							displayMessage(url.toString());
							try {
								audioPlayer.queue(url);
							}
							catch (Exception e) {
								System.out.println("An error occured: " + e);
							}
						}
					}
				}
				// Handle pausing music playback
				else if (args[0].equals("pause")) {
					// Bot must have something queued to play
					if (audioPlayer.getCurrentTrack() == null){
						displayMessage("There is nothing playing");
					}
					// Playback already paused
					else if (audioPlayer.isPaused()) {
						displayMessage("Music playback is already paused!");
					}
					// Pause music playback
					else {
						audioPlayer.setPaused(true);
						displayMessage("Playback paused");
					}
				}
				// Handle resuming music playback
				else if (args[0].equals("play") || args[0].equals("resume")) {
					if (audioPlayer.getPlaylistSize() == 0 || audioPlayer.getCurrentTrack() == null) {
						displayMessage("There is nothing (queued) to play");
					}
					else {
						audioPlayer.setPaused(false);
						displayMessage("Resuming playback");
					}
				}
				// Handle clearing audio queue
				else if (args[0].equals("clear")) {
					// Bot must have something queued to clear
					if (audioPlayer.getPlaylistSize() != 0) {
						audioPlayer.clear();
						displayMessage("Queue Cleared.");
					} else {
						displayMessage("Nothing to clear");
					}
				}
				// Handle skipping current track
				else if (args[0].equals("skip")) {
					// Bot must have something queued to play
					if (audioPlayer.getPlaylistSize() == 0 ||  audioPlayer.getCurrentTrack() == null) {
						displayMessage("There is nothing to skip");
					}
					else {
						audioPlayer.skip(); // Skip the current track
					}
				}
				// Handle setting volume
				else if (args[0].equals("volume")) {
					try {
						float volume = Float.parseFloat(args[1]);
						if (volume < 0 || volume > 100) {
							displayMessage(args[1] + " is not within the range [0, 100], please try again");
						}
						else {
							volume *= VOL_CONST;
							volume = (float) Math.pow(10, volume) - 1;
							audioPlayer.setVolume(volume);
							displayMessage("Volume set to " + args[1]);
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						displayMessage("You did not input a number to set volume to, please try again");
					} catch(NumberFormatException e) {
						displayMessage(args[1] + " is not a valid number, please try again");
					}
				}
				// Handle repeating songs
				else if (args[0].equals("repeat")) {
					try {
						int songNum = Integer.parseInt(args[1]);
						if (songNum < 1 || songNum > 5) {
							displayMessage(args[1] + " is not within the range [1, 5], please try again");
						}
						else {
              				try {
								audioPlayer.queue(dlYoutubeMP3(new URL(songRepeatList[songNum-1][1])));
							} catch(Exception e) {
                				displayMessage("Error:" + e.getMessage());
              				}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						displayMessage(displayRepeatList()); // Just display list of last 5 songs played if no number was entered
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

	private File dlYoutubeMP3(URL url) {
		/**
		 * Queues a music file from a youtube url
		 */
		File musicFile = null;
		try {
			// Download youtube and convert to mp3 using youtube-dl
			String dl_dir = "youtube-dl -o " + System.getProperty("user.dir");
      		dl_dir += "/%(title)s.%(ext)s -x --audio-format mp3 " + url;
      		Process py = Runtime.getRuntime().exec(dl_dir);
      		System.out.println("Downloading song file...");
			displayMessage("Downloading...");
			BufferedReader in = new BufferedReader(new InputStreamReader(py.getInputStream()));
			String input;
			String title = null;
			boolean correct_line;
			input = in.readLine();
			py.waitFor();

			// Load the mp3 file into the bot
			File dir = new File(System.getProperty("user.dir"));
			for (File file : dir.listFiles()) {
				if (file.getName().endsWith(".mp3")) {
					System.out.println("Download completed. Loading song file...");
					prevSong = musicFile;
					musicFile = file;
					System.out.println("Song loaded: \"" + musicFile.getName().substring(0, musicFile.getName().length() - 4) + "\"");
				}
			}
		} catch (Exception e) {
			displayMessage("Youtube Load Error: " + e.getMessage());
		}
		return musicFile;
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

	/**
	 * Return the currently-playing song's name
	 */
	public String getSongName() {
		return currentSong.getName().substring(0, currentSong.getName().length() - 4);
	}

	/**
	 * Display the last 5 songs played by the
     * bot for user to choose to repeat
	 */
	private String displayRepeatList() {
		String songList = "Last 5 songs played: \n";
		for (int song = 0; song < 5; song++) {
			songList += "\n"+(song+1)+". "+songRepeatList[song][0];
		}
		return songList;
	}

	/**
	 * Add the current queued song to the array 
     * of unique songs that can be repeated
	 */
	private void addSongToRepeatList(String songName, String songUrl) {
		// Check if song is from Youtube, otherwise don't add it for now
		if (songUrl.contains("youtube.com")) {
			// If it is a unique song (different from the last song, which is not null), then add it to the list
			if (!(songRepeatList[0][1] != null && songRepeatList[0][1].equals(songUrl))) {
				int startAt = 3; // Initialize as second-last index in case repeat list is full
				// Find where the first unique song is
				for (int song = 0; song <= 4 ; song++) {
					// If the repeat list is full
					if (song == 4) {
						/*
						// Delete the file at the end of the list
						try {
							Files.delete(musicFile.toPath());
						} catch (Exception x) {
							displayMessage("Error deleting file: " + x.getMessage());
						}
						*/
					} else if (songRepeatList[song][1] == null) {
					// If the repeat list is not full (found an empty slot)
						startAt = song;
						break;
					}
				}
				for (int song = startAt; song >= 0; song--) {
					// Unique song: shift all songs on list down one and add the song to the front of the array
					songRepeatList[song+1][0] = songRepeatList[song][0];
					songRepeatList[song+1][1] = songRepeatList[song][1];
				}
				songRepeatList[0][0] = songName;
				songRepeatList[0][1] = songUrl;
				System.out.println("Unique song added to repeat list");
			}
		}
	}

	/**
	 * Delete the downloaded file of the previous song when a new song is queued
	 */
	private void deletePreviousSong() {
		// Delete the file at the end of the list
		try {
			if (prevSong != null) {
				Files.delete(prevSong.toPath());
			}
		} catch (Exception x) {
			displayMessage("Error deleting file: " + x.getMessage());
		}
	}

}
