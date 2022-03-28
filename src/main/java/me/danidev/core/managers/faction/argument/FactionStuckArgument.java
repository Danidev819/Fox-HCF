package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.timer.type.StuckTimer;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.Faction;

public class FactionStuckArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionStuckArgument(final Main plugin) {
        super("stuck", "Teleport to a safe location.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this commands from the overworld.");
            return true;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if (factionAt instanceof EventFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
            return true;
        }
        final StuckTimer stuckTimer = this.plugin.getTimerManager().getStuckTimer();
        if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Your " + stuckTimer.getName() + ChatColor.RED + " timer is already active.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + stuckTimer.getName() + ChatColor.YELLOW + " timer has started. Teleportation will commence in " + ChatColor.LIGHT_PURPLE + DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.YELLOW + ". This will cancel if you move more than " + 5 + " blocks.");
        return true;
    }
}
