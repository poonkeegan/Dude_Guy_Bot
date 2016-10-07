## Changes/Fixes
* Fixed audio queuing, AudioPlayer playlist, *audio repeat*
* Fixed bot status not clearing when AudioPlayer.clear() is called
* Fixed bot sometimes not displaying song name as its "Playing:" status

## Currently-known bugs
*Currently testing for bugs*


## Changelog
#10.07.2016
* Fixed audio queuing, AudioPlayer playlist, *audio repeat*
* Changed to using a custom AudioPlayer that dispatches a custom *PlaylistClearedEvent* event when the playlist is cleared (AudioPlayer.clear() is called), which will remove the song name "Playing:" status

#10.06.2016
* Updated Discord4J version from 2.5.2 to 2.6.1
* Updated audio system from sx.blah.discord.handle.AudioChannel (Removed/Depreciated) to sx.blah.discord.util.audio.AudioPlayer
* Bot will display a game **"Playing:"** status that displays the name of the current song that is playing

Added *kc* command
* Type "kc kanmusu *shipgirl_name*" to bring up stats of the shipgirl
* Type "kc equip *equipment_name*" to bring up stats of the equipment

Added *bully* command
* Type "bully *mention*" to bully that mentioned person

Added *praise* command
* Type "praise *mention*" to praise that mentioned person
* Type "praise *mention_1 mention_2 ... mention_n*" to praise those mentioned people

Added *roll* command
* Type "roll" to roll a 6-sided die
* Type "roll *num_die_faces*" to roll a custom multi-sided die (Number of faces must be 6 or larger)
* Type "roll *num_die_faces num_times*" to roll a custom multi-sided die (Number of faces must be 6 or larger) a specified number of times

Added *toss* command
* Type "toss" to toss a coin and obtain a random result of "Heads" or "Tails".

Added *help* command
* Type "help" to bring up a list of available commands
* Type "help *command*" to bring up a list of available parameters for that command 

Additionally, the bot will kek whenever someone keks
