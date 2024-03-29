package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.service.ConfigurationService;
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

public class FactionLeaderArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionLeaderArgument(final Main plugin) {
        super("leader", "Set a faction leader.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
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
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        final FactionMember selfMember = playerFaction.getMember(uuid);
        final Role selfRole = selfMember.getRole();
        if (selfRole != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be the current faction leader to transfer the faction.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' is not in your faction.");
            return true;
        }
        if (targetMember.getUniqueId().equals(uuid)) {
            sender.sendMessage(ChatColor.RED + "You are already the faction leader.");
            return true;
        }
        targetMember.setRole(Role.LEADER);
        selfMember.setRole(Role.CAPTAIN);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOR + selfMember.getRole().getAstrix() + selfMember.getName() + ChatColor.YELLOW + " has transferred leadership of the faction to " + ConfigurationService.TEAMMATE_COLOR + targetMember.getRole().getAstrix() + targetMember.getName() + ChatColor.YELLOW + '.');
        return true;
    }
    
    @SuppressWarnings({ "deprecation", "unused" })
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
        final Map<UUID, FactionMember> members = playerFaction.getMembers();
        for (final Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            final OfflinePlayer target;
            final String targetName;
            if (entry.getValue().getRole() != Role.LEADER && (targetName = (target = Bukkit.getOfflinePlayer((UUID)entry.getKey())).getName()) != null) {
                if (results.contains(targetName)) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
