## Changes/Fixes
Fixed and removed repeat code for *audio repeat*


## Updated
Discord4j version from 2.5.2 to 2.6.1
Audio system from sx.blah.discord.handle.AudioChannel (Removed/Depreciated) to sx.blah.discord.util.audio.AudioPlayer


## Added
*kc* command
..* Type "kc kanmusu *shipgirl_name*" to bring up stats of the shipgirl
..* Type "kc equip *equipment_name*" to bring up stats of the equipment

*bully* command
..* Type "bully *mention*" to bully that mentioned person

*praise* command
..* Type "praise *mention*" to praise that mentioned person
..* Type "praise *mention_1 mention_2 ... mention_n*" to praise those mentioned people

*roll* command
..* Type "roll" to roll a 6-sided die
..* Type "roll *num_die_faces*" to roll a custom multi-sided die (Number of faces must be 6 or larger)
..* Type "roll *num_die_faces num_times*" to roll a custom multi-sided die (Number of faces must be 6 or larger) a specified number of times

*deck* command
..* Type "deck draw" to draw a card from the standard 52-card deck, return its string representation, and place it at the bottom of the deck
..* Type "deck shuffle" to shuffle the standard 52-card deck

*rps* command (Unfinished)
..* Type "rps @mention" to play "rock-paper-scissors" against the mentioned person. The bot will message each player for their play choice, and will output the results once both players responded with "rock", "paper", or "scissors" (not case-sensitive).

*toss* command
..* Type "toss" to toss a coin and obtain a random result of "Heads" or "Tails".

*help* command
..* Type "help" to bring up a list of available commands
..* Type "help *command*" to bring up a list of available parameters for that command 

Additionally, the bot will kek whenever someone keks
