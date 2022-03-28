package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.OfflinePlayer;
import java.util.Set;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionDemoteArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionDemoteArgument(final Main plugin) {
        super("demote", "Demote a faction member.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a officer to edit the roster.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "That player is not in your faction.");
            return true;
        }
        if (targetMember.getRole() != Role.CAPTAIN) {
            sender.sendMessage(ChatColor.RED + "You can only demote faction captains.");
            return true;
        }
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + targetMember.getName() + ChatColor.YELLOW + " has been demoted from a faction " + targetMember.getRole().toString().toLowerCase() + ".");
        targetMember.setRole(Role.MEMBER);
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            return Collections.emptyList();
        }
        final ArrayList<String> results = new ArrayList<String>();
        final Set<UUID> keySet = playerFaction.getMembers().keySet();
        for (final UUID entry : keySet) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
            final String targetName = target.getName();
            if (targetName != null) {
                if (playerFaction.getMember(target.getUniqueId()).getRole() != Role.CAPTAIN) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
