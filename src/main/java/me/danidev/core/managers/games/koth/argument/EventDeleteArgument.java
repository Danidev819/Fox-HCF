package me.danidev.core.managers.games.koth.argument;

import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.games.koth.faction.EventFaction;

public class EventDeleteArgument extends CommandArgument {
	
	private final Main plugin;

	public EventDeleteArgument(final Main plugin) {
		super("delete", "Deletes an event");
		this.plugin = plugin;
		this.permission = "fhcf.commands.event.argument." + this.getName();
	}

	public String getUsage(final String label) {
		return "/" + label + ' ' + this.getName() + " <eventName>";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: "
					+ ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
		if (!(faction instanceof EventFaction)) {
			sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
			return true;
		}
		if (this.plugin.getFactionManager().removeFaction(faction, sender)) {
			sender.sendMessage(ChatColor.YELLOW + "Deleted event faction " + ChatColor.AQUA
					+ faction.getDisplayName(sender) + ChatColor.YELLOW + '.');
		}
		return true;
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (args.length != 2) {
			return Collections.emptyList();
		}
		return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
	}
}
