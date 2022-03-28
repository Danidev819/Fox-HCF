package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;

import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class CoordsCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public CoordsCommand(Main plugin) {
        plugin.getCommand("coords").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        langConfig.getStringList("COORDS").forEach(lines ->
                sender.sendMessage(CC.translate(lines)));
        return true;
    }
}
