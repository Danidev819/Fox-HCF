package me.danidev.core.commands.chat;

import me.danidev.core.Main;
import me.danidev.core.commands.chat.argument.ChatClearArgument;
import me.danidev.core.commands.chat.argument.ChatMuteArgument;
import me.danidev.core.commands.chat.argument.ChatSlowArgument;
import me.danidev.core.utils.command.ArgumentExecutor;

public class ChatExecutor extends ArgumentExecutor {

    public ChatExecutor(Main plugin) {
        super("chat");

        this.addArgument(new ChatMuteArgument());
        this.addArgument(new ChatClearArgument());
        this.addArgument(new ChatSlowArgument());

        plugin.getCommand("chat").setExecutor(this);
    }
}
