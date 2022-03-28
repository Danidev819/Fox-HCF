package me.danidev.core.commands.network;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StoreCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public StoreCommand(Main plugin) {
        plugin.getCommand("store").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        sender.sendMessage(CC.translate(langConfig.getString("STORE")
                .replace("%STORE%", ConfigurationService.STORE)));
        return true;
    }
}
