package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionOpenArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionOpenArgument(final Main plugin) {
        super("open", "Open your faction.");
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
        final FactionMember factionMember = playerFaction.getMember(player.getUniqueId());
        if (factionMember.getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to do this.");
            return true;
        }
        final boolean newOpen = !playerFaction.isOpen();
        playerFaction.setOpen(newOpen);
        playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " has " + (newOpen ? (ChatColor.GREEN + "opened") : (ChatColor.RED + "closed")) + ChatColor.YELLOW + " the faction to public.");
        return true;
    }
}
