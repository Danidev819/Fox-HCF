package me.danidev.core.managers.games.koth.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.games.koth.EventTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventCancelArgument extends CommandArgument {
	private final Main plugin;

	public EventCancelArgument(final Main plugin) {
		super("cancel", "Cancels a running event");
		this.plugin = plugin;
		this.permission = "fhcf.commands.event.argument." + this.getName();
	}

	public String getUsage(final String label) {
		return "/" + label + ' ' + this.getName();
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
		final EventFaction eventFaction = eventTimer.getEventFaction();

		if (!eventTimer.clearCooldown()) {
			sender.sendMessage(ChatColor.RED + "There is not a running event.");
			return true;
		}

		if (eventFaction instanceof KothFaction) {
			KothFaction kothFaction = (KothFaction) eventFaction;
			Location location = kothFaction.getCaptureZone().getCuboid().getCenter();
			Main.get().getWaypointManager().deleteWaypoint(eventFaction.getName(), location, -3355444);
		}
		else if (eventFaction instanceof CitadelFaction) {
			CitadelFaction citadelFaction = (CitadelFaction) eventFaction;
			Location location = citadelFaction.getCaptureZone().getCuboid().getCenter();
			Main.get().getWaypointManager().deleteWaypoint(eventFaction.getName(), location, -65281);
		}

		Bukkit.broadcastMessage(CC.translate("&d" + sender.getName() + " &ehas cancelled " + ((eventFaction == null) ? "the active event" : ("&b" + eventFaction.getName() + "&e."))));
		return true;
	}
}
