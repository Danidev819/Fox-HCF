package me.danidev.core.commands.teleport;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand implements CommandExecutor {

    public WorldCommand(Main plugin) {
        plugin.getCommand("world").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(CC.translate("&cUsage: /" + command.getLabel() + " <world>"));
            return true;
        }

        try {
            if (player.getLocation().getWorld().getName().equals(args[0])) {
                player.sendMessage(CC.translate("&cYou're already in this world."));
                return true;
            }

            Location location = new Location(Bukkit.getWorld(args[0]), 0, 100, 0);

            player.teleport(location);
            player.sendMessage(CC.translate("&eYou've been teleport to &6" + args[0] + "&e."));
        }
        catch (Exception ex) {
            player.sendMessage(CC.translate("&cWorld '" + args[0] + "' not found."));
        }
        return true;
    }
}
