package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionCreateArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionCreateArgument(final Main plugin) {
        super("create", "Create a faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <factionName>";
    }

    @Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands may only be executed by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        String name = args[1];

        if (name.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + 3 + " characters.");
            return true;
        }
        if (name.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + 16 + " characters.");
            return true;
        }
        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }
        if (this.plugin.getFactionManager().getFaction(name) != null) {
            sender.sendMessage(ChatColor.RED + "Faction '" + name + "' already exists.");
            return true;
        }
        if (this.plugin.getFactionManager().getPlayerFaction((Player)sender) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        this.plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);
        return true;
    }
}
