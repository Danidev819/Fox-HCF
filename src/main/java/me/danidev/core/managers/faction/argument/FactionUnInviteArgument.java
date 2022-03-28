package me.danidev.core.managers.faction.argument;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionUnInviteArgument extends CommandArgument {
	
    private static final ImmutableList<String> COMPLETIONS;
    private final Main plugin;
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
    
    public FactionUnInviteArgument(final Main plugin) {
        super("uninvite", "Revoke an invite to a player.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <all|playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-invite from a faction.");
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
        final FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to un-invite players.");
            return true;
        }
        final Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
        if (args[1].equalsIgnoreCase("all")) {
            invitedPlayerNames.clear();
            sender.sendMessage(ChatColor.YELLOW + "You have cleared all pending invitations.");
            return true;
        }
        if (!invitedPlayerNames.remove(args[1])) {
            sender.sendMessage(ChatColor.RED + "There is not a pending invitation for " + args[1] + '.');
            return true;
        }
        playerFaction.broadcast(ChatColor.YELLOW + factionMember.getRole().getAstrix() + sender.getName() + " has uninvited " + ConfigurationService.ENEMY_COLOR + args[1] + ChatColor.YELLOW + " from the faction.");
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }
        final ArrayList<String> results = new ArrayList<String>((Collection<? extends String>) FactionUnInviteArgument.COMPLETIONS);
        results.addAll(playerFaction.getInvitedPlayerNames());
        return results;
    }
}
