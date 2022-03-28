package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionCoLeaderArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionCoLeaderArgument(final Main plugin) {
        super("coleader", "Set a faction coleader.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return String.valueOf('/') + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set faction leaders.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        final FactionMember selfMember = playerFaction.getMember(uuid);
        final Role selfRole = selfMember.getRole();
        if (selfRole != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be an leader to assign the coleader role to an member.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' is not in your faction.");
            return true;
        }
        if (targetMember.getRole().equals(Role.COLEADER)) {
            sender.sendMessage(ChatColor.RED + "This member is already a co-leader!");
            return true;
        }
        if (targetMember.getUniqueId().equals(uuid)) {
            sender.sendMessage(ChatColor.RED + "You are the leader, which means you cannot co-leader yourself.");
            return true;
        }
        targetMember.setRole(Role.COLEADER);
        playerFaction.broadcast(ChatColor.GREEN + targetMember.getName() + ChatColor.YELLOW + " has been promoted to a co-leader.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() != Role.COLEADER) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        final Map<UUID, FactionMember> members = playerFaction.getMembers();
        for (final Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            if (entry.getValue().getRole() != Role.LEADER) {
                final OfflinePlayer target = Bukkit.getOfflinePlayer((UUID)entry.getKey());
                final String targetName = target.getName();
                if (targetName == null) {
                    continue;
                }
                if (results.contains(targetName)) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
