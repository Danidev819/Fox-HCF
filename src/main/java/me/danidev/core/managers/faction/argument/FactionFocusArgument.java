package me.danidev.core.managers.faction.argument;

import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.danidev.core.managers.faction.type.PlayerFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionFocusArgument extends CommandArgument {

    public FactionFocusArgument() {
        super("focus", "Focus a faction.");
    }

    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <player>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }

        PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

        if (!offlinePlayer.hasPlayedBefore()) {
            player.sendMessage(CC.translate("&cPlayer '" + args[1] + "' not found."));
            return true;
        }

        PlayerFaction targetFaction = Main.get().getFactionManager().getPlayerFaction(offlinePlayer.getUniqueId());

        if (targetFaction == null) {
            player.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }

        if (playerFaction == targetFaction) {
            player.sendMessage(ChatColor.RED + "You can not focus your own faction.");
            return true;
        }

        if (playerFaction.getFocused() != null) {
            if (playerFaction.getFocused().getHome() != null) {
                Main.get().getWaypointManager().deleteFactionWaypoint(playerFaction, playerFaction.getFocused().getName(), playerFaction.getFocused().getHome(), -65536);
            }
            playerFaction.broadcast(CC.translate("&9" + playerFaction.getFocused().getName() +" &ehas been unfocused by &d" + player.getName() + "&e."));
            playerFaction.setFocused(null);
        	return true;
        }

        playerFaction.setFocused(targetFaction);

        if (playerFaction.getFocused().getHome() != null) {
            Main.get().getWaypointManager().createFactionWaypoint(playerFaction, playerFaction.getFocused().getName(), playerFaction.getFocused().getHome(), -65536);
        }

        playerFaction.broadcast(CC.translate("&9" + targetFaction.getName() +" &ehas been focused by &d" + player.getName() + "&e."));
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }

        Player player = (Player)sender;
        ArrayList<String> results = new ArrayList<>(Main.get().getFactionManager().getFactionNameMap().keySet());

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target)) {
                if (!results.contains(target.getName())) {
                    results.add(target.getName());
                }
            }
        }
        return results;
    }
}
