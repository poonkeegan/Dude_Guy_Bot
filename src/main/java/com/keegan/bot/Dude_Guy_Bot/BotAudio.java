package com.keegan.bot.Dude_Guy_Bot;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.audio.AudioPlayer.Track;
import sx.blah.discord.util.audio.events.AudioPlayerEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;


public class BotAudio extends Command {

	private static final float VOL_CONST = 2.7f/2500;
	private static String[][] songRepeatList = new String[5][2];
	private static MusicPlayer audioPlayer;

	public BotAudio(MusicPlayer audioPlayer) {
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
			
			// No parameters passed
			if (args.length == 0) {
				displayMessage("No parameters passed.");
			}
			else {
				if (args[0].equals("queue")) {
					URL url = processURL(args);
					if (url == null) {
						// Display playlist
						String playlistMessage = "There are currently " + audioPlayer.getPlaylistSize() + " songs queued";
						if (audioPlayer.getPlaylistSize() > 0) {
							playlistMessage += ":\n\n";
						}
						Track[] playlist = audioPlayer.getPlaylist().toArray(new Track[audioPlayer.getPlaylistSize()]);
						for (int playlistIndex = 0; playlistIndex < playlist.length; playlistIndex++) {
							String songName = playlist[playlistIndex].getMetadata().toString().replace("{file=" + System.getProperty("user.dir") + '/', "").replace(".mp3}", "\n");
							playlistMessage += (playlistIndex + 1) + ". **" + songName + "**";
						}
						displayMessage(playlistMessage);
					}
					else if (url != null) {
						// Queue up the url from Youtube and Niconico
						if (args[1].contains("youtube.com") || args[1].contains("nicovideo.jp")) {
							try {
								File songToQueue = dlSongMP3(url);
								audioPlayer.queue(songToQueue);
								String songName = songToQueue.getName().substring(0, songToQueue.getName().indexOf('-'));
								addSongToRepeatList(songName, url.toString());
								displayMessage("**" + songName + "** queued");
							}
							catch (Exception e) {
								System.out.println("An error occured while queuing a song: " + e);
								displayMessage("Please check the URL or try a different one.");
							}
						}
						// Queue up a direct file (.mp3, .ogg, .flac and .wav supported)
						else if (args[1].contains(".mp3") || args[1].contains(".ogg") || args[1].contains(".flac") || args[1].contains(".wav")) {
							try {
								String fileURL = url.toString();
								String songName = fileURL.substring(fileURL.lastIndexOf('/'), fileURL.lastIndexOf('.'));
								System.out.println("Song name: " + songName);
								audioPlayer.queue(url);
								addSongToRepeatList(songName, url.toString());
								displayMessage("**" + songName + "** queued");
							}
							catch (Exception e) {
								System.out.println("An error occured while queuing a direct file link: " + e);
								displayMessage("Please check the URL or try a different one.");
							}
						}
						// Get the audio from the video on specified website to queue
						else {
							displayMessage(url.toString());
							try {
								audioPlayer.queue(url);
							}
							catch (Exception e) {
								System.out.println("An error occured while queuing the link: " + e);
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
						displayMessage("Playback paused.");
					}
				}
				// Handle resuming music playback
				else if (args[0].equals("play") || args[0].equals("resume")) {
					if (audioPlayer.getPlaylistSize() == 0 || audioPlayer.getCurrentTrack() == null) {
						displayMessage("There is nothing (queued) to play");
					}
					else {
						audioPlayer.setPaused(false);
						displayMessage("Resuming playback.");
					}
				}
				// Handle clearing audio queue
				else if (args[0].equals("clear")) {
					// Bot must have something queued to clear
					if (audioPlayer.getPlaylistSize() != 0) {
						audioPlayer.clear();
						displayMessage("Playlist Cleared.");
					} else {
						displayMessage("The playlist is empty. Nothing to clear.");
					}
				}
				// Handle skipping current track
				else if (args[0].equals("skip")) {
					// Bot must have something queued to play
					if (audioPlayer.getPlaylistSize() == 0 ||  audioPlayer.getCurrentTrack() == null) {
						displayMessage("There is nothing to skip");
					}
					else {
						String currSongName = audioPlayer.getCurrentTrack().getMetadata().toString().replace("{file=" + System.getProperty("user.dir") + '/', "").replace(".mp3}", "\n");
						audioPlayer.skip(); // Skip the current track
						displayMessage("**" + currSongName + "** skipped.");
					}
				}
				// Handle setting volume
				else if (args[0].equals("volume")) {
					try {
						float volume = Float.parseFloat(args[1]);
						if (volume < 0 || volume > 100) {
							displayMessage(args[1] + " is not within the range [0, 150], please try again");
						}
						else {
							audioPlayer.setVolume((float)(volume/100.0));
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
								audioPlayer.queue(dlSongMP3(new URL(songRepeatList[songNum-1][1])));
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

	private File dlSongMP3(URL url) {
		/**
		 * Queues a music file from a youtube url
		 */
		File musicFile = null;
		try {
			// Download youtube and convert to mp3 using youtube-dl
			String dl_dir = "youtube-dl -o " + System.getProperty("user.dir");
      		dl_dir += "/%(title)s-%(id)s.%(ext)s -x --audio-format mp3 " + url;
			Process py = Runtime.getRuntime().exec(dl_dir);
			System.out.println("Downloading song file...");
			displayMessage("Downloading...");
			BufferedReader in = new BufferedReader(new InputStreamReader(py.getInputStream()));
			String input;
			input = in.readLine();
			py.waitFor();
			System.out.println("Download finished.");

			// Load the mp3 file into the bot
			File dir = new File(System.getProperty("user.dir"));
			for (File file : dir.listFiles()) {
				// Niconico & YT
				if (file.getName().contains(url.toString().substring(url.toString().indexOf("watch") + 8, url.toString().length())) && file.getName().endsWith(".mp3")) {
					System.out.println("Loading file...");
					musicFile = file; // Load the new file
					System.out.println("Song loaded: \"" + musicFile.getName().substring(0, musicFile.getName().indexOf('-')) + "\"");
				}
			}
		}
		catch (Exception e) {
			displayMessage("Error: " + e.getMessage());
		}

		return musicFile;
	}

	private URL processURL(String[] args) {
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
			//displayMessage("No URL provided");
		} catch (MalformedURLException e) {
			displayMessage("Invalid URL provided");
		}
		return url;
	}

	/**
	 * Return the currently-playing song
	 */
	public File getCurrSong() {
		return new File(audioPlayer.getCurrentTrack().getMetadata().toString().replace("{file=", "").replace("}", "\n"));
	}

	/**
	 * Display the last 5 songs played by the
     * bot for user to choose to repeat
	 */
	private String displayRepeatList() {
		String songList = "Last 5 songs played: \n";
		for (int song = 0; song < 5; song++) {
			songList += "\n" + (song+1) + ". **" + songRepeatList[song][0] + "**";
		}
		return songList;
	}

	/**
	 * Add the current queued song to the array 
     * of unique songs that can be repeated
	 */
	private void addSongToRepeatList(String songName, String songUrl) {
		// Check if song is from Youtube, otherwise don't add it for now
		if (songUrl.contains("youtube.com") || songUrl.contains("nicovideo.jp")) {
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

}
