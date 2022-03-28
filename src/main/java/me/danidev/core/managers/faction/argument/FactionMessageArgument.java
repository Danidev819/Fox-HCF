package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.struct.ChatChannel;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionMessageArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionMessageArgument(final Main plugin) {
        super("message", "Send a message to your faction.", new String[]{"message"});
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <message>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use faction chat.");
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
        final String format = String.format(ChatChannel.FACTION.getRawFormat(player), "", StringUtils.join((Object[])args, ' ', 1, args.length));
        for (final Player target : playerFaction.getOnlinePlayers()) {
            target.sendMessage(format);
        }
        return true;
    }
}
