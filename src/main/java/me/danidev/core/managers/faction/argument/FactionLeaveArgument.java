package me.danidev.core.managers.faction.argument;

import java.util.UUID;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionLeaveArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionLeaveArgument(final Main plugin) {
        super("leave", "Leave from your faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can leave faction.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        if (playerFaction.getMember(uuid).getRole() == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You cannot leave factions as a leader. Either use " + ChatColor.GOLD + '/' + label + " disband" + ChatColor.RED + " or " + ChatColor.GOLD + '/' + label + " leader" + ChatColor.RED + '.');
            return true;
        }
        if (playerFaction.setMember(player, null)) {
            sender.sendMessage(ChatColor.YELLOW + "Successfully left the faction.");
            playerFaction.broadcast(Relation.ENEMY.toChatColour() + sender.getName() + ChatColor.YELLOW + " has left the faction.");
        }
        return true;
    }
}
