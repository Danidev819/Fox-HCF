package me.danidev.core.managers.faction.argument.staff;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.struct.ChatChannel;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionForceJoinArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionForceJoinArgument(final Main plugin) {
        super("forcejoin", "Force join to a faction.");
        this.plugin = plugin;
        this.permission = "fhcf.commands.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <factionName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can join factions.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only join player factions.");
            return true;
        }
        playerFaction = (PlayerFaction)faction;
        if (playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER), true)) {
            playerFaction.broadcast(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + sender.getName() + " has forcefully joined the faction.");
        }
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
        final ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
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
