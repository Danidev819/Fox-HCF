package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.chat.ClickAction;
import me.danidev.core.utils.chat.Text;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Statistic;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class StatsCommand implements CommandExecutor {

    public StatsCommand(Main plugin) {
        plugin.getCommand("stats").setExecutor(this);
    }

    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player)sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' not found.");
            return true;
        }

        this.sendInformation(player, Bukkit.getOfflinePlayer(args[0]));
        return true;
    }
    
    public void sendInformation(Player player, OfflinePlayer target) {
        FactionUser factionUser = Main.get().getUserManager().getUser(target.getUniqueId());

        int targetKills = target.getPlayer().getStatistic(Statistic.PLAYER_KILLS);
        int targetDeaths = target.getPlayer().getStatistic(Statistic.DEATHS);

        int targetLives = Main.get().getLivesManager().getLives(target.getUniqueId());

        player.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        player.sendMessage(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "Stats Statistics");
        player.sendMessage("");

        if (Main.get().getFactionManager().getPlayerFaction(target.getUniqueId()) != null) {
            player.sendMessage(Main.get().getFactionManager().getPlayerFaction(target.getUniqueId()).getRelation(player).toChatColour() + ChatColor.YELLOW.toString() + " Player: " + ChatColor.WHITE + target.getName());
            new Text(ChatColor.YELLOW + " Faction: " + Main.get().getFactionManager().getPlayerFaction(target.getUniqueId()).getDisplayName(player))
                    .setHoverText(ChatColor.YELLOW + "Click to view Faction")
                    .setClick(ClickAction.RUN_COMMAND, "/f who " + Main.get().getFactionManager().getPlayerFaction(target.getUniqueId()).getName()).send(player);
        }
        else {
            player.sendMessage(ChatColor.YELLOW + " Player: " + ChatColor.WHITE + target.getName());
        }

        player.sendMessage(ChatColor.YELLOW + " Kills: " + ChatColor.WHITE + targetKills);
        player.sendMessage(ChatColor.YELLOW + " Deaths: " + ChatColor.WHITE + targetDeaths);
        player.sendMessage(ChatColor.YELLOW + " Balance: " + ChatColor.LIGHT_PURPLE + "$" + Main.get().getEconomyManager().getBalance(target.getUniqueId()));
        player.sendMessage(ChatColor.YELLOW + " Available Lives: " + ChatColor.WHITE + targetLives);

        if (factionUser.getDeathban() != null) {
            player.sendMessage(ChatColor.YELLOW + " Deathbanned: " + (factionUser.getDeathban().isActive() ? (ChatColor.GREEN + "True") : (ChatColor.RED + "False")));
        }
        else if (!Main.get().isKitMap()) {
            player.sendMessage(ChatColor.YELLOW + " Deathbanned: " + ChatColor.RED + "False");
        }

        player.sendMessage(ChatColor.YELLOW + " Playtime: " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(Main.get().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true));
        player.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }
}
