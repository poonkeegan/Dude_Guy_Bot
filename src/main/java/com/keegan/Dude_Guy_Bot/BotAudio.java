package com.keegan.Dude_Guy_Bot;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

public class BotAudio extends Command {



	static final float VOL_CONST = 2.7f/5000;
	
	public void run() {
		/**
		 * Implement commands for playing audio
		 */
		// Check for permissions
		if (isAdmin()) {
			// Get arguments passed
			String[] args = getArgs();
			// Handle no parameters

			if(args.length == 0){
				displayMessage("No parameters passed");
				String toDisp = "Correct usage " + Instance.getKey(message) + Instance.getCmd(message) + " \"parameters\"";
				displayMessage(toDisp);
			} 
			
			else {
				// Initialize audio channel
				AudioChannel audio_chn = loadCurrChn();
				
				if(args[0].equals("queue")){
					URL url = processYoutubeURL(args);
					File music = null;
					if (url != null) {
						// Queue up the url
						if (args[1].contains("youtube.com")){
							// Get the youtube audio to queue
							music = loadYoutubeMP3(url, audio_chn);
							audio_chn.queueFile(music);
							displayMessage(music.getName());
							// Delete the downloaded youtube file
							try{
								Files.delete(music.toPath());
							}catch (IOException x){
								displayMessage(x.getMessage());
							}
						}else{
							displayMessage(url.toString());
							audio_chn.queueUrl(url);
						}
						displayMessage(music.getName() + " queued.");
					}
				} 
				// Handle pausing music
				else if (args[0].equals("pause")){
					// Bot must have something queued to play
					if(audio_chn.getQueueSize() == 0){
						displayMessage("There is nothing playing");
					}
					else{
						audio_chn.pause();
					}
				}
				
				else if (args[0].equals("play")){
					if(audio_chn.getQueueSize() == 0){
						displayMessage("There is nothing to play currently.");
					}
					else{
						audio_chn.resume();
					}
				}
				
				else if (args[0].equals("clear")){
					// Bot must have something queued to clear
					if(audio_chn.getQueueSize() != 0){
						audio_chn.clearQueue();
						displayMessage("Queue Cleared.");
					}else{
						displayMessage("Nothing to clear");
					}
				}
				
				else if (args[0].equals("skip")){
					// Bot must have something queued to play
					if(audio_chn.getQueueSize() == 0){
						displayMessage("There is nothing to skip currently.");
					}
					else{
						audio_chn.skip();
						displayMessage("Song Skipped.");
					}
				}
				
				else if (args[0].equals("volume")){
					try{
						float volume = Float.parseFloat(args[1]);
						if (volume < 0 || volume > 100){
							displayMessage(args[1] + " is not within the range [0, 100], please try again");
						}
						else{
							volume *= VOL_CONST;
							volume = (float) Math.pow(10, volume) - 1;
							audio_chn.setVolume(volume);
							displayMessage("Volume set to " + args[1]);
						}
					}catch(ArrayIndexOutOfBoundsException e){
						displayMessage("You did not input a number to set volume to, please try again");
					}catch(NumberFormatException e){
						displayMessage(args[1] + " is not a valid number, please try again");
					}
				}
			}

		}else{
			displayMessage("No permission");
		}

	}

	public void joinChannel(){
		/**
		 * Attempts to have the bot join the voice channel that user is in
		 */
		IVoiceChannel curr_chn = null;
		try{
			// Get channel of sender
			curr_chn = message.getAuthor().getVoiceChannel().get();
			// Connect to sender's channel
			if (!bot.getConnectedVoiceChannels().contains(curr_chn)){
				curr_chn.join();
			}
		} 
		// Make sure that there is a channel to join to
		catch (NoSuchElementException e){
			displayMessage("You are not currently in a valid channel to perform this command");
		}
	}

	private File loadYoutubeMP3(URL url, AudioChannel audio_chn){
		/**
		 * Queues a music file from a youtube url
		 */
		File music = null;
		try {
			// Download youtube and convert to mp3 using youtube-dl
			Process py = Runtime.getRuntime().exec("python ../youtube-dl -x --audio-format mp3 " + url);
			displayMessage("Downloading File");
			BufferedReader in = new BufferedReader(new InputStreamReader(py.getInputStream()));
			String input;
			String title = null;
			boolean correct_line;
			input = in.readLine();
			do{
				correct_line = input.startsWith("[download] Destination: ");
				if (correct_line){
					// Gets Title name, figure out what to do with this
					title = processYoutubeTitle(input);
				}
				input = in.readLine();
			}while((!(input == null) || correct_line));
			py.waitFor();
			displayMessage("Now Loading File");

			// Load the mp3 file into the bot
			File dir = new File(System.getProperty("user.dir"));
			for (File file : dir.listFiles()){
				if (file.getName().endsWith(".mp3")){
					music = file;
				}
			}
		} catch (Exception e) {
			displayMessage(e.getMessage());
		}
		return music;
	}

	private String processYoutubeTitle(String input){
		/**
		 * Strips the video title out of a download log message
		 */
		int startIndex = "[download] Destination: ".length();
		int endIndex = input.lastIndexOf('.');
		String title = input.substring(startIndex, endIndex);
		return title;
	}
	
	private URL processYoutubeURL(String[] args){
		// Initialize the given url
		URL url = null;
		try {
			// Remove extra parameters in the url
			int cutoff = args[1].indexOf('&');
			if (cutoff == -1){
				cutoff = args[1].length();
			}
			url = new URL(args[1].substring(0, cutoff));
		} catch(ArrayIndexOutOfBoundsException e){
			displayMessage("No URL provided");
		} catch (MalformedURLException e) {
			displayMessage("Invalid URL");
		}
		return url;
	}
	
	private AudioChannel loadCurrChn(){
		AudioChannel curr_chn = null;
		try{
			curr_chn = bot.getGuilds().get(0).getAudioChannel();
		}catch(DiscordException e){
			displayMessage(e.getErrorMessage());
		}
		return curr_chn;
	}
}
