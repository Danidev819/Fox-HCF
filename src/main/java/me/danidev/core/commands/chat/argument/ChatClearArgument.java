package me.danidev.core.commands.chat.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ChatClearArgument extends CommandArgument {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public ChatClearArgument() {
        super("clear", "Chat Clear");
        this.permission = "fhcf.command.chat.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (int i = 0; i < 100; ++i) {
            Bukkit.broadcastMessage("");
        }
        Bukkit.broadcastMessage(CC.translate(langConfig.getString("CHAT.CLEAR")
                .replace("%PLAYER%", sender.getName())));
        return true;
    }
}
