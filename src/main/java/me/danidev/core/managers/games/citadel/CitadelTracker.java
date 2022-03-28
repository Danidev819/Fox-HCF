package me.danidev.core.managers.games.citadel;

import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventTimer;
import me.danidev.core.managers.games.koth.EventType;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.tracker.EventTracker;
import me.danidev.core.utils.DateTimeFormats;
import org.bukkit.entity.Player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;

public class CitadelTracker implements EventTracker {
	
	public static long DEFAULT_CAP_MILLIS;
	private static final long MINIMUM_CONTROL_TIME_ANNOUNCE1;
	private final Main plugin;

	static {
		MINIMUM_CONTROL_TIME_ANNOUNCE1 = TimeUnit.SECONDS.toMillis(25L);
	}

	public CitadelTracker(Main plugin) {
		this.plugin = plugin;
		CitadelTracker.DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(20L);	}

	@Override
	public EventType getEventType() {
		return EventType.CITADEL;
	}

	@Override
	public void tick(EventTimer eventTimer, EventFaction eventFaction) {
		CaptureZone captureZone = ((CitadelFaction) eventFaction).getCaptureZone();
		long remainingMillis = captureZone.getRemainingCaptureMillis();
		
		if (remainingMillis <= 0L) {
			this.plugin.getTimerManager().eventTimer.handleWinnerCitadel(captureZone.getCappingPlayer());
			eventTimer.clearCooldown();
			return;
		}

		if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
			return;
		}
		
		int remainingSeconds = (int) (remainingMillis / 1000L);
		if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName()
					+ "] " + ChatColor.GOLD + "Someone is controlling " + ChatColor.LIGHT_PURPLE
					+ captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '('
					+ DateTimeFormats.PALACE_FORMAT.format(remainingMillis) + ')');
		}
	}

	@Override
	public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
		Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName()
				+ "] " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.GOLD + " can now be contested. "
				+ ChatColor.RED + '(' + DateTimeFormats.PALACE_FORMAT.format(eventTimer.getRemaining1()) + ')');
	}

	@Override
	public boolean onControlTake(Player player, CaptureZone captureZone) {
		player.sendMessage(ChatColor.GOLD + "You are now in control of " + ChatColor.LIGHT_PURPLE
				+ captureZone.getDisplayName() + ChatColor.GOLD + '.');
		return true;
	}

	@Override
	public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
		player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
		long remainingMillis = captureZone.getRemainingCaptureMillis();
		if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > CitadelTracker.MINIMUM_CONTROL_TIME_ANNOUNCE1) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName()
					+ "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of "
					+ ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED
					+ " (" + DateTimeFormats.PALACE_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
		}
		return true;
	}

	@Override
	public void stopTiming() {
	}
}
