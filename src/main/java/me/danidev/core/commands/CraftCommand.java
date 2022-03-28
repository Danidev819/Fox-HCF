package me.danidev.core.commands;

import me.danidev.core.Main;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class CraftCommand implements CommandExecutor {

    public CraftCommand(Main plugin) {
        plugin.getCommand("craft").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player)sender;

        player.openWorkbench(player.getLocation(), true);
        return true;
    }
}
