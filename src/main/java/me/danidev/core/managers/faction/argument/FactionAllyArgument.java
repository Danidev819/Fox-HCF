package me.danidev.core.managers.faction.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Collection;

import me.danidev.core.Main;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.event.FactionRelationCreateEvent;
import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionAllyArgument extends CommandArgument {
	
    private static final Relation RELATION;
    private final Main plugin;
    
    static {
        RELATION = Relation.ALLY;
    }
    
    public FactionAllyArgument(final Main plugin) {
        super("ally", "Make an ally pact with other factions.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <factionName>";
    }

    @Override
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
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be an officer to make relation wishes.");
            return true;
        }
        final Faction containingFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (!(containingFaction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        final PlayerFaction targetFaction = (PlayerFaction)containingFaction;
        if (playerFaction.equals(targetFaction)) {
            sender.sendMessage(ChatColor.RED + "You cannot send " + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.RED + " requests to your own faction.");
            return true;
        }
        final Collection<UUID> allied = playerFaction.getAllied();
        if (allied.size() >= ConfigurationService.MAX_ALLIES_PER_FACTION) {
            sender.sendMessage(ChatColor.RED + "Your faction cant have more allies than " + ConfigurationService.MAX_ALLIES_PER_FACTION + '.');
            return true;
        }
        if (targetFaction.getAllied().size() >= ConfigurationService.MAX_ALLIES_PER_FACTION) {
            sender.sendMessage(String.valueOf(targetFaction.getDisplayName(sender)) + ChatColor.RED + " has reached their maximum alliance limit, which is " + ConfigurationService.MAX_ALLIES_PER_FACTION + '.');
            return true;
        }
        if (allied.contains(targetFaction.getUniqueID())) {
            sender.sendMessage(ChatColor.RED + "Your faction already is " + FactionAllyArgument.RELATION.getDisplayName() + 'd' + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
            return true;
        }
        if (targetFaction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null) {
            final FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, targetFaction, FactionAllyArgument.RELATION);
            Bukkit.getPluginManager().callEvent((Event)event);
            targetFaction.getRelations().put(playerFaction.getUniqueID(), FactionAllyArgument.RELATION);
            targetFaction.broadcast(ChatColor.YELLOW + "Your faction is now " + FactionAllyArgument.RELATION.getDisplayName() + 'd' + ChatColor.YELLOW + " with " + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + '.');
            playerFaction.getRelations().put(targetFaction.getUniqueID(), FactionAllyArgument.RELATION);
            playerFaction.broadcast(ChatColor.YELLOW + "Your faction is now " + FactionAllyArgument.RELATION.getDisplayName() + 'd' + ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            return true;
        }
        if (playerFaction.getRequestedRelations().putIfAbsent(targetFaction.getUniqueID(), FactionAllyArgument.RELATION) != null) {
            sender.sendMessage(ChatColor.YELLOW + "Your faction has already requested to " + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            return true;
        }
        playerFaction.broadcast(String.valueOf(targetFaction.getDisplayName(playerFaction)) + ChatColor.YELLOW + " were informed that you wish to be " + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + '.');
        targetFaction.broadcast(String.valueOf(playerFaction.getDisplayName(targetFaction)) + ChatColor.YELLOW + " has sent a request to be " + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + ". Use " + ConfigurationService.ALLY_COLOR + "/faction " + this.getName() + ' ' + playerFaction.getName() + ChatColor.YELLOW + " to accept.");
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.emptyList();
        }
        final ArrayList<String> results = new ArrayList<>();
        return results;
    }
}
