package me.danidev.core.commands;

import me.danidev.core.utils.CC;
import org.bukkit.*;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.player.PlayerTeleportEvent;

import me.danidev.core.Main;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class SpawnCommand implements CommandExecutor {

    public SpawnCommand(Main plugin) {
        plugin.getCommand("spawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player)sender;

        if (player.getGameMode().equals(GameMode.CREATIVE) || player.hasPermission("fhcf.command.spawn.bypass")) {
            player.teleport(new Location(Bukkit.getWorld(Main.get().getLocationsConfig().getString("SPAWN.WORLD")), Main.get().getLocationsConfig().getInt("SPAWN.X"), Main.get().getLocationsConfig().getInt("SPAWN.Y"), Main.get().getLocationsConfig().getInt("SPAWN.Z"), Main.get().getLocationsConfig().getInt("SPAWN.YAW"), Main.get().getLocationsConfig().getInt("SPAWN.PITCH")));
            player.sendMessage(CC.translate("&eYou have been teleported to spawn."));
            return true;
        }

        if (Main.get().getTimerManager().getSpawnTagTimer().getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
            return false;
        }

        Main.get().getTimerManager().getTeleportTimer().teleport(player, new Location(Bukkit.getWorld(Main.get().getLocationsConfig().getString("SPAWN.WORLD")), Main.get().getLocationsConfig().getInt("SPAWN.X"), Main.get().getLocationsConfig().getInt("SPAWN.Y"), Main.get().getLocationsConfig().getInt("SPAWN.Z"), Main.get().getLocationsConfig().getInt("SPAWN.YAW"), Main.get().getLocationsConfig().getInt("SPAWN.PITCH")), TimeUnit.SECONDS.toMillis(15L), ChatColor.YELLOW + "Teleporting to spawn in " + ChatColor.LIGHT_PURPLE + "15 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }
}
