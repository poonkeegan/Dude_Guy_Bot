package com.keegan.Dude_Guy_Bot;

import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class CommandListener
{

    // This is the executor that we'll look for
    final static String KEY = "!";

    public CommandListener(IDiscordClient client)
    {
        client.getDispatcher().registerListener(this);
    }

    @EventSubscriber
    public void watchForCommands(MessageReceivedEvent event)
    {
        try
        {

            IMessage _message = event.getMessage();
            String _content = _message.getContent();

            if (!_content.startsWith(KEY))
               return;

            String _command = _content.toLowerCase();
            String[] _args = null;

            if (_content.contains(" "))
            {
                _command = _command.split(" ")[0];
                _args = _content.substring(_content.indexOf(' ') + 1).split(" ");
            }

        }
        catch (Exception ex)
        {
            // Handle how ever you please
        }
    }

}