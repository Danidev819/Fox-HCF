package me.danidev.core.commands.inventory;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.Bukkit;

public class InvSeeCommand implements CommandExecutor {
    
    public InvSeeCommand(Main plugin) {
        plugin.getCommand("invsee").setExecutor(this);
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(CC.translate("&cUsage: /" + label + " <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        player.openInventory(target.getInventory());
        return true;
    }
}
