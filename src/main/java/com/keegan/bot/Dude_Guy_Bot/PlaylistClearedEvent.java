package com.keegan.bot.Dude_Guy_Bot;

import sx.blah.discord.util.audio.events.AudioPlayerEvent;

/**
 * This event is dispatched when AudioPlayer.clean() is called on an AudioPlayer instance.
 */
public class PlaylistClearedEvent extends AudioPlayerEvent {

	public PlaylistClearedEvent(MusicPlayer player) {
		super(player);
	}

}
