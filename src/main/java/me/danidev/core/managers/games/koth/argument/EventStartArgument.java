package me.danidev.core.managers.games.koth.argument;

import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.danidev.core.managers.faction.type.Faction;

public class EventStartArgument extends CommandArgument {
	private final Main plugin;

	public EventStartArgument(final Main plugin) {
		super("start", "Starts an event");
		this.plugin = plugin;
		this.permission = "fhcf.commands.event.argument." + this.getName();
	}

	public String getUsage(final String label) {
		return "/" + label + ' ' + this.getName() + " <eventName>";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
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

		EventFaction eventFaction = (EventFaction) faction;

		if (this.plugin.getTimerManager().eventTimer.tryContesting(eventFaction, sender)) {
			if (eventFaction instanceof KothFaction) {
				KothFaction kothFaction = (KothFaction) eventFaction;
				Location location = kothFaction.getCaptureZone().getCuboid().getCenter();
				Main.get().getWaypointManager().createWaypoint(faction.getName(), location, -3355444);
			}
			else if (eventFaction instanceof CitadelFaction) {
				CitadelFaction citadelFaction = (CitadelFaction) eventFaction;
				Location location = citadelFaction.getCaptureZone().getCuboid().getCenter();
				Main.get().getWaypointManager().createWaypoint(faction.getName(), location, -65281);
			}
			sender.sendMessage(ChatColor.YELLOW + "Successfully contested " + faction.getName() + '.');
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
