package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.apache.commons.lang3.time.DurationFormatUtils;
import java.util.concurrent.TimeUnit;

public class FactionRenameArgument extends CommandArgument {
	
    private static final long FACTION_RENAME_DELAY_MILLIS;
    private static final String FACTION_RENAME_DELAY_WORDS;
    private final Main plugin;
    
    static {
        FACTION_RENAME_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(15L);
        FACTION_RENAME_DELAY_WORDS = DurationFormatUtils.formatDurationWords(FactionRenameArgument.FACTION_RENAME_DELAY_MILLIS, true, true);
    }
    
    public FactionRenameArgument(final Main plugin) {
        super("rename", "Rename your faction name.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <newFactionName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create faction.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        @SuppressWarnings("deprecation")
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to edit the name.");
            return true;
        }

        final String newName = args[1];

        if (newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + 3 + " characters.");
            return true;
        }
        if (newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + 16 + " characters.");
            return true;
        }
        if (!JavaUtils.isAlphanumeric(newName)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }
        if (this.plugin.getFactionManager().getFaction(newName) != null) {
            sender.sendMessage(ChatColor.RED + "Faction " + newName + ChatColor.RED + " already exists.");
            return true;
        }
        final long difference = playerFaction.lastRenameMillis - System.currentTimeMillis() + FactionRenameArgument.FACTION_RENAME_DELAY_MILLIS;
        if (!player.isOp() && difference > 0L) {
            player.sendMessage(ChatColor.RED + "There is a faction rename delay of " + FactionRenameArgument.FACTION_RENAME_DELAY_WORDS + ". Therefore you need to wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + " to rename your faction.");
            return true;
        }
        playerFaction.setName(args[1], sender);
        return true;
    }
}
