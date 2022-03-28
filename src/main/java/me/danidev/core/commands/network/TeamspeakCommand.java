package me.danidev.core.commands.network;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class TeamspeakCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public TeamspeakCommand(Main plugin) {
        plugin.getCommand("teamspeak").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        sender.sendMessage(CC.translate(langConfig.getString("TEAMSPEAK")
                .replace("%TEAMSPEAK%", ConfigurationService.TEAMSPEAK)));
        return true;
    }
}
