package me.danidev.core.managers.games.koth.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.utils.DateTimeFormats;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.games.koth.EventTimer;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventUptimeArgument extends CommandArgument {
	private final Main plugin;

	public EventUptimeArgument(final Main plugin) {
		super("uptime", "Check the uptime of an event");
		this.plugin = plugin;
		this.permission = "fhcf.commands.event.argument." + this.getName();
	}

	public String getUsage(final String label) {
		return "/" + label + ' ' + this.getName();
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
		if (eventTimer.getRemaining() <= 0L) {
			sender.sendMessage(ChatColor.RED + "There is not a running event.");
			return true;
		}
		final EventFaction eventFaction;
		sender.sendMessage(ChatColor.YELLOW + "Up-time of " + eventTimer.getName() + " timer"
				+ (((eventFaction = eventTimer.getEventFaction()) == null) ? ""
						: (": " + ChatColor.BLUE + '(' + eventFaction.getDisplayName(sender) + ChatColor.BLUE + ')'))
				+ ChatColor.YELLOW + " is " + ChatColor.GRAY
				+ DurationFormatUtils.formatDurationWords(eventTimer.getUptime(), true, true) + ChatColor.YELLOW
				+ ", started at " + ChatColor.GOLD
				+ DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + ChatColor.YELLOW + '.');
		return true;
	}
}
