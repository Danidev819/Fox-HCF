package me.danidev.core.commands.teleport;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportHereCommand implements CommandExecutor {
	
    public TeleportHereCommand(Main plugin) {
        plugin.getCommand("teleporthere").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        
        if (args.length < 1) {
            player.sendMessage(CC.translate("&cUsage: /" + label + " <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        target.teleport(player.getLocation());
        player.sendMessage(CC.translate("&eYou have teleported &6" + target.getName() + " &eto you."));
        return true;
    }
}
