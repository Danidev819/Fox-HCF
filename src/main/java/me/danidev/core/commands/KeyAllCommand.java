package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.menu.keyall.KeyAllMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeyAllCommand implements CommandExecutor {

    public KeyAllCommand(Main plugin) {
        plugin.getCommand("keyall").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        new KeyAllMenu().openMenu(player);
        return false;
    }
}
