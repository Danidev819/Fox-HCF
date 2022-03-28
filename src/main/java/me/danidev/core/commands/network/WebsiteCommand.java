package me.danidev.core.commands.network;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class WebsiteCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public WebsiteCommand(Main plugin) {
        plugin.getCommand("website").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        sender.sendMessage(CC.translate(langConfig.getString("WEBSITE")
                .replace("%WEBSITE%", ConfigurationService.WEBSITE)));
        return true;
    }
}
