package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class OresCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public OresCommand(Main plugin) {
        plugin.getCommand("ores").setExecutor(this);
    }
	
    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if (!player.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' not found.");
            return true;
        }

        String playerName = player.getName();

        int emerald = player.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE);
        int diamond = player.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);
        int gold = player.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE);
        int iron = player.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE);
        int redstone = player.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE);
        int lapis = player.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE);
        int coal = player.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE);

        langConfig.getStringList("ORES").forEach(
                lines -> sender.sendMessage(CC.translate(lines
                    .replace("%PLAYER%", playerName)
                    .replace("%EMERALD%", String.valueOf(emerald))
                    .replace("%DIAMOND%", String.valueOf(diamond))
                    .replace("%GOLD%", String.valueOf(gold))
                    .replace("%IRON%", String.valueOf(iron))
                    .replace("%REDSTONE%", String.valueOf(redstone))
                    .replace("%LAPIS%", String.valueOf(lapis))
                    .replace("%COAL%", String.valueOf(coal)))));
        return true;
    }
}
