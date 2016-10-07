package com.keegan.bot.Dude_Guy_Bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.events.*;


/**
 * austinv11's AudioPlayer, edited to throw a "PlaylistClearedEvent" when the clear() method is called
 */
public class MusicPlayer extends AudioPlayer {

	private IGuild guild;
	private IDiscordClient client;


	public MusicPlayer(IAudioManager manager) {
		super(manager);
		this.client = manager.getGuild().getClient();
	}

	public MusicPlayer(IGuild guild) {
		this(guild.getAudioManager());
		this.guild = guild;
	}

	public void clear() {
		super.clear();
		client.getDispatcher().dispatch(new PlaylistClearedEvent(this));
	}

}
