package com.keegan.bot.Dude_Guy_Bot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;


public class BotAudio extends Command {

	static final float VOL_CONST = 2.7f/2500;
	static String[][] songRepeatList = new String[5][2];
  private MusicList queuedList = new MusicList();
	public void run() {
		/**
		 * Implement commands for playing audio
		 */
		// Check for permissions
		if (hasPerms()) {
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
					queueAudio(args[1], audio_chn);
				}
				// Handle pausing music
				else if (args[0].equals("pause")) {
					// Bot must have something queued to play
					if (audio_chn.getQueueSize() == 0){
						displayMessage("There is nothing playing");
					}
					else {
						audio_chn.pause();
					}
				}

				else if (args[0].equals("play")) {
					if (audio_chn.getQueueSize() == 0) {
						displayMessage("There is nothing to play currently.");
					}
					else {
						audio_chn.resume();
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
					try {
						int songNum = Integer.parseInt(args[1]);
						if (songNum < 1 || songNum > 5) {
							displayMessage(args[1] + " is not within the range [1, 5], please try again");
						}
						else {
							try {
								queueYoutubeSong(audio_chn, music, new URL(songRepeatList[songNum-1][1];
              } catch(Exception e) {
                displayMessage(e.getMessage());
              }
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						displayMessage(queuedList.toString()); // Just display list of last 5 songs played if no number was entered
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
		} catch (Exception e){
      displayMessage(e.getMessage());
    }
	}

	private void queueAudio(String music_url, AudioChannel audio_chn){
		/**
		* Given a URL to a music file or a youtube link, queues music
		*/
		// Check if it's a youtube link to process
		// Compressed youtube should always work
		Url url;
		if (music_url.contains("youtu.be")){
			try {
				url = new URL(youtube_link.substring(0, cutoff));
			} catch (MalformedURLException e) {
				displayMessage("Invalid URL");
			}
			queueYoutubeSong(audio_chn, url);
		}else if(music_url.contains("www.youtube.com/watch?v=")){
			// Otherwise, process it then queue it
			url = processYoutubeURL(music_url);
			queueYoutubeSong(audio_chn, url);
		}else {
			// Queue music files
			url = processURL(music_url);
			audio_chn.queueUrl(url);
			addSongToMusicList(music_url);
		}
		displayMessage(queuedList.getName() + " queued.");
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
				}
			}
		} catch (Exception e) {
			displayMessage(e.getMessage());
		}
		return music;
	}

	private URL processYoutubeURL(String youtube_link) {
		// Initialize the given url
		URL url = null;

			// Remove extra parameters in the url
			int cutoff = youtube_link.indexOf('&');
			if (cutoff == -1) {
				cutoff = youtube_link.length();
			}
			url = processURL(youtube_link.substring(0, cutoff));
		return url;
	}

	private URL processURL(String link){
		URL url;
		try {
			url = new URL(link);
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
	private void queueYoutubeSong(AudioChannel audio_chn, URL url) {
		// Get the audio from the YT video to queue
		File music = loadYoutubeMP3(url, audio_chn);
		audio_chn.queueFile(music);
		addSongToMusicList(music.getName(), url.toString());
		// Delete the downloaded youtube file
		try {
			Files.delete(music.toPath());
		} catch (IOException x) {
			displayMessage(x.getMessage());
		}
	}

	private void displayMusicList(){
		displayMessage(queuedList.toString());
	}

	private void addSongToMusicList(String song_title, String song_loc){
		queuedList.push(song_title, song_loc);
	}

	private void addSongToMusicList(String song_loc){
		queuedList.push(null , song_loc);
	}
	/**
	 * Display the last 5 songs played by the
     * bot for user to choose to repeat
	 */
	private String displayRepeatList() {
		String songList = "Last 5 songs played: ";
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
						if (!(songRepeatList[song-1][1].equals(songUrl))) {
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
