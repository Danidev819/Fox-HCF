package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class StatsResetCommand implements CommandExecutor {

    public StatsResetCommand(Main plugin) {
        plugin.getCommand("statsreset").setExecutor(this);
    }

    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if (!player.hasPlayedBefore() && !player.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        player.getPlayer().setStatistic(Statistic.PLAYER_KILLS, 0);
        player.getPlayer().setStatistic(Statistic.DEATHS, 0);

        Main.get().getUserManager().getUser(player.getUniqueId()).setKills(0);
        Main.get().getUserManager().getUser(player.getUniqueId()).setDeaths(0);

        sender.sendMessage(CC.translate("&eYou have reset &6" + player.getName() + " &estatistics."));
        return true;
    }
}
