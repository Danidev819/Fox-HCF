package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class EnderchestCommand implements CommandExecutor {

    public EnderchestCommand(Main plugin) {
        plugin.getCommand("enderchest").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0 || !player.hasPermission("fhcf.command.enderchest.others")) {
            player.openInventory(player.getEnderChest());
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        player.openInventory(target.getEnderChest());
        return true;
    }
}
