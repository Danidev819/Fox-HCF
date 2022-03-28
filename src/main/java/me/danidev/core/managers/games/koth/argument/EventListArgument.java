package me.danidev.core.managers.games.koth.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import java.util.stream.Collectors;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;

public class EventListArgument extends CommandArgument {
	private final Main plugin;

	public EventListArgument(final Main plugin) {
		super("list", "Check the uptime of an event");
		this.plugin = plugin;
		this.permission = "fhcf.commands.event.argument." + this.getName();
	}

	public String getUsage(final String label) {
		return "/" + label + ' ' + this.getName();
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		final List<Faction> events = this.plugin.getFactionManager().getFactions().stream()
				.filter(faction -> faction instanceof EventFaction).collect(Collectors.toList());
		sender.sendMessage(ChatColor.GREEN + "Current events:");
		for (final Faction factionEvent : events) {
			sender.sendMessage(ChatColor.GREEN + factionEvent.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.YELLOW
					+ this.getFactionEventType(factionEvent) + ChatColor.DARK_GRAY + ")");
		}
		return true;
	}

	private String getFactionEventType(final Faction factionEvent) {
		if (factionEvent instanceof KothFaction) {
			return "Koth";
		}
		if (factionEvent instanceof CitadelFaction) {
			return "Palace";
		}
		return "Conquest";
	}
}
