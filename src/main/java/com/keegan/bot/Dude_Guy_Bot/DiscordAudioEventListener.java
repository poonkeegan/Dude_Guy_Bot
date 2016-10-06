package com.keegan.bot.Dude_Guy_Bot;

import java.util.EventListener;

import sx.blah.discord.util.audio.events.*;


/**
 * DiscordAudioEventListener
 * 
 * Interface class to listen for Discord AudioPlayer events
 */
interface DiscordAudioEventListener extends EventListener {

	/**
	 * This is dispatched when AudioPlayer.clean() is called on an 
	 * AudioPlayer instance, removing all references to it from the 
	 * IAudioManager it is associated to. Also loads the previously 
	 * used IAudioProvider and IAudioProcessor.
	 */
	public void audioPlayerCleaned(AudioPlayerCleanEvent event);


	/**
	 * This represents an event which is dispatched by an AudioPlayer.
	 */
	public void audioPlayerEvent(AudioPlayerEvent event);


	/**
	 * This is dispatched when an AudioPlayer instance is initialized.
	 */
	public void audioPlayerInitialized(AudioPlayerInitEvent event);


	/**
	 * This is fired whenever AudioPlayer.setLoop(boolean) is called, 
	 * determining whether the AudioPlayer should loop its playlist or not.
	 */
	public void audioPlaylistLooped(LoopStateChangeEvent event);


	/**
	 * This is fired whenever AudioPlayer.setPaused(boolean) is called, 
	 * determining whether the AudioPlayer is paused or not.
	 */
	public void audioPlayerPaused(PauseStateChangeEvent event);


	/**
	 * This is dispatched whenever AudioPlayer.addProcessor(IAudioProcessor) 
	 * is called, adding an IAudioProcessor to the AudioPlayer.
	 */
	public void audioProcessorAdded(ProcessorAddEvent event);


	/**
	 * This is dispatched whenever AudioPlayer.removeProcessor(IAudioProcessor) 
	 * is called, removing an IAudioProcessor from the AudioPlayer
	 */
	public void audioProcessorRemoved(ProcessorRemoveEvent event);


	/**
	 * This is dispatched when AudioPlayer.shuffle() is called, shuffling the 
	 * AudioPlayer's playlist queue.
	 */
	public void audioPlaylistShuffled(ShuffleEvent event);


	/**
	 * This is dispatched when a track is finished playing
	 */
	public void audioFinishedPlaying(TrackFinishEvent event);


	/**
	 * This is dispatched whenever a track is skipped.
	 */
	public void audioSkipped(TrackSkipEvent event);


	/**
	 * This is dispatched when a track starts playing.
	 */
	public void audioStartedPlaying(TrackStartEvent event);


	/**
	 * This is dispatched when AudioPlayer.setVolume(float) is called, 
	 * changing the volume of the AudioPlayer. Only works on tracks 
	 * with an AudioInputStream rather than a direct IAudioProvider.
	 */
	public void audioVolumeChanged(VolumeChangeEvent event);

}
