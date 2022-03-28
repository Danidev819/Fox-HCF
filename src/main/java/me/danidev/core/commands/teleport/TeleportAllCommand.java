package me.danidev.core.commands.teleport;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAllCommand implements CommandExecutor {

    public TeleportAllCommand(Main plugin) {
        plugin.getCommand("teleportall").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        Bukkit.getOnlinePlayers().forEach(online -> {
            if (!online.equals(player)) online.teleport(player.getLocation());
        });

        player.sendMessage(CC.translate("&eAll players have been teleported to your location."));
        return true;
    }
}
