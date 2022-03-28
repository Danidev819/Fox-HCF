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

import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionPromoteArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionPromoteArgument(final Main plugin) {
        super("promote", "Promote a faction member.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set faction captains.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(uuid).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to assign members as a captain.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "That player is not in your faction.");
            return true;
        }
        if (targetMember.getRole() != Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You can only assign captains to members, " + targetMember.getName() + " is a " + targetMember.getRole().getName() + '.');
            return true;
        }
        final Role role = Role.CAPTAIN;
        targetMember.setRole(role);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + role.getAstrix() + targetMember.getName() + ChatColor.YELLOW + " has been assigned as a faction captain.");
        return true;
    }
    
    @SuppressWarnings({ "unused", "deprecation" })
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
        for (final Map.Entry<UUID, FactionMember> entry : playerFaction.getMembers().entrySet()) {
            if (entry.getValue().getRole() == Role.MEMBER) {
                final OfflinePlayer target;
                final String targetName;
                if ((targetName = (target = Bukkit.getOfflinePlayer((UUID)entry.getKey())).getName()) == null) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
