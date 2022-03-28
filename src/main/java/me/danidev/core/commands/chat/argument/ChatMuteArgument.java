package me.danidev.core.commands.chat.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.file.FileConfig;
import me.danidev.core.handlers.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ChatMuteArgument extends CommandArgument {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public ChatMuteArgument() {
        super("mute", "Mute Chat");
        this.permission = "fhcf.command.chat.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (ChatHandler.isMuted()) {
            ChatHandler.setChatToggled(false);
            Bukkit.broadcastMessage(CC.translate(langConfig.getString("CHAT.MUTE")
                    .replace("%PLAYER%", sender.getName())));
        }
        else {
            ChatHandler.setChatToggled(true);
            Bukkit.broadcastMessage(CC.translate(langConfig.getString("CHAT.UN_MUTE")
                    .replace("%PLAYER%", sender.getName())));
        }
        return true;
    }
}
