package me.danidev.core.managers.faction.argument.staff;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.entity.Player;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionSetDtrArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionSetDtrArgument(final Main plugin) {
        super("setdtr", "Set a faction DTR.");
        this.plugin = plugin;
        this.permission = "fhcf.command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName|factionName> <newDtr>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        Double newDTR = JavaUtils.tryParseDouble(args[2]);
        if (newDTR == null) {
            final String s = args[2];
            final String s2;
            switch (s2 = s) {
                case "-d": {
                    final Faction factiondecrearse = this.plugin.getFactionManager().getContainingFaction(args[1]);
                    final PlayerFaction playerFactiondecrearse = (PlayerFaction)factiondecrearse;
                    final double previousDtrdecrearse = playerFactiondecrearse.getDeathsUntilRaidable();
                    playerFactiondecrearse.setDeathsUntilRaidable(previousDtrdecrearse - 1.0);
                    sender.sendMessage(ChatColor.YELLOW + "You have decreased the DTR of " + playerFactiondecrearse.getName() + " by 1.");
                    return true;
                }
                case "-i": {
                    final Faction factionincrease = this.plugin.getFactionManager().getContainingFaction(args[1]);
                    final PlayerFaction playerFactionincrease = (PlayerFaction)factionincrease;
                    final double previousDtr = playerFactionincrease.getDeathsUntilRaidable();
                    playerFactionincrease.setDeathsUntilRaidable(previousDtr + 1.0);
                    sender.sendMessage(ChatColor.YELLOW + "You have increased the DTR of " + playerFactionincrease.getName() + " by 1.");
                    return true;
                }
                default:
                    break;
            }
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            for (final Faction faction : this.plugin.getFactionManager().getFactions()) {
                if (!(faction instanceof PlayerFaction)) {
                    continue;
                }
                ((PlayerFaction)faction).setDeathsUntilRaidable(newDTR);
            }
            sender.sendMessage(CC.translate("&eSet DTR of all factions to " + newDTR + "."));
            return true;
        }
        final Faction faction2 = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction2 == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (!(faction2 instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only set DTR of player factions.");
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction)faction2;
        final double previousDtr2 = playerFaction.getDeathsUntilRaidable();
        newDTR = playerFaction.setDeathsUntilRaidable(newDTR);
        sender.sendMessage(CC.translate("&eSet DTR of " + faction2.getName() + " from " + previousDtr2 + " to " + newDTR + "."));
        return true;
    }
    
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final ArrayList<String> results = new ArrayList<>(this.plugin.getFactionManager().getFactionNameMap().keySet());
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
