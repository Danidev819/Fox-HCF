package me.danidev.core.managers.faction.argument;

import java.util.Set;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionInvitesArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionInvitesArgument(final Main plugin) {
        super("invites", "Check faction invites.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return String.valueOf('/') + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can have faction invites.");
            return true;
        }
        final List<String> receivedInvites = new ArrayList<String>();
        for (final Faction faction : this.plugin.getFactionManager().getFactions()) {
            if (faction instanceof PlayerFaction) {
                final PlayerFaction targetPlayerFaction = (PlayerFaction)faction;
                if (!targetPlayerFaction.getInvitedPlayerNames().contains(sender.getName())) {
                    continue;
                }
                receivedInvites.add(targetPlayerFaction.getDisplayName(sender));
            }
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(((Player)sender).getUniqueId());
        final String delimiter = ChatColor.WHITE + ", " + ChatColor.GRAY;
        if (playerFaction != null) {
            final Set<String> sentInvites = playerFaction.getInvitedPlayerNames();
            sender.sendMessage(ChatColor.YELLOW + "Sent by " + playerFaction.getDisplayName(sender) + ChatColor.YELLOW + " (" + sentInvites.size() + ')' + ChatColor.YELLOW + ": " + ChatColor.GRAY + (sentInvites.isEmpty() ? "Your faction has not invited anyone." : (String.valueOf(StringUtils.join(sentInvites, delimiter)) + '.')));
        }
        sender.sendMessage(ChatColor.YELLOW + "Requested (" + receivedInvites.size() + ')' + ChatColor.YELLOW + ": " + ChatColor.GRAY + (receivedInvites.isEmpty() ? "No factions have invited you." : (String.valueOf(StringUtils.join(receivedInvites, new StringBuilder().append(ChatColor.WHITE).append(delimiter).toString())) + '.')));
        return true;
    }
}
