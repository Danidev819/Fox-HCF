package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionDisbandArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionDisbandArgument(final Main plugin) {
        super("disband", "Disband your faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.isRaidable() && !this.plugin.getEotwHandler().isEndOfTheWorld()) {
            sender.sendMessage(ChatColor.RED + "You cannot disband your faction while it is raidable.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a leader to disband the faction.");
            return true;
        }
        this.plugin.getFactionManager().removeFaction(playerFaction, sender);
        return true;
    }
}
