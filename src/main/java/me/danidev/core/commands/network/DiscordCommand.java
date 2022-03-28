package me.danidev.core.commands.network;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class DiscordCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public DiscordCommand(Main plugin) {
        plugin.getCommand("discord").setExecutor(this);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        sender.sendMessage(CC.translate(langConfig.getString("DISCORD")
                .replace("%DISCORD%", ConfigurationService.DISCORD)));
        return true;
    }
}
