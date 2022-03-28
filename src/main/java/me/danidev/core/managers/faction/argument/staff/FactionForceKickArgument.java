package me.danidev.core.managers.faction.argument.staff;

import java.util.Collections;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionForceKickArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionForceKickArgument(final Main plugin) {
        super("forcekick", "Force kick faction member.");
        this.plugin = plugin;
        this.permission = "fhcf.commands.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getContainingPlayerFaction(args[1]);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        final FactionMember factionMember = playerFaction.getMember(args[1]);
        if (factionMember == null) {
            sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (factionMember.getRole() == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You cannot forcefully kick faction leaders.");
            return true;
        }
        if (playerFaction.setMember(factionMember.getUniqueId(), null, true)) {
            playerFaction.broadcast(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + factionMember.getName() + " has been forcefully kicked by " + sender.getName() + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
