package me.danidev.core.managers.games.koth;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.managers.games.koth.faction.ConquestFaction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.managers.timer.GlobalTimer;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.danidev.core.managers.faction.event.CaptureZoneEnterEvent;
import me.danidev.core.managers.faction.event.CaptureZoneLeaveEvent;
import me.danidev.core.managers.faction.type.PlayerFaction;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class EventTimer extends GlobalTimer implements Listener {

	private static final long RESCHEDULE_FREEZE_MILLIS;
	private static final String RESCHEDULE_FREEZE_WORDS;
	private final Main plugin;
	private long startStamp;
	private long lastContestedEventMillis;
	private EventFaction eventFaction;

	public EventTimer(Main plugin) {
		super("Event", 0L);
		this.plugin = plugin;

		new BukkitRunnable() {
			public void run() {
				if (EventTimer.this.eventFaction != null) {
					EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
				}
			}
		}.runTaskTimer(plugin, 20L, 20L);
	}

	public EventFaction getEventFaction() {
		return this.eventFaction;
	}

	public String getScoreboardPrefix() {
		return ChatColor.translateAlternateColorCodes('&', "&9&l");
	}


	public String getName() {
		return (this.eventFaction == null) ? "Event" : (ChatColor.BOLD + this.eventFaction.getName());
	}

	@Override
	public boolean clearCooldown() {
		boolean result = super.clearCooldown();
		if (this.eventFaction != null) {
			for (CaptureZone captureZone : this.eventFaction.getCaptureZones()) {
				captureZone.setCappingPlayer(null);
			}
			this.eventFaction.setDeathban(true);
			this.eventFaction.getEventType().getEventTracker().stopTiming();
			this.eventFaction = null;
			this.startStamp = -1L;
			result = true;
		}
		return result;
	}

	@EventHandler
	public void onDecay(LeavesDecayEvent e) {
		if (this.plugin.getFactionManager().getFactionAt(e.getBlock()) != null) {
			e.setCancelled(true);
		}
	}

	@Override
	public long getRemaining() {
		if (this.eventFaction == null) {
			return 0L;
		}
		if (this.eventFaction instanceof KothFaction) {
			return ((KothFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
		}
		return super.getRemaining();
	}

	public long getRemaining1() {
		if (this.eventFaction == null) {
			return 0L;
		}
		if (this.eventFaction instanceof CitadelFaction) {
			return ((CitadelFaction) this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
		}
		return super.getRemaining();
	}

	public void handleWinnerKoth(Player winner) {
		if (this.eventFaction == null) return;

		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
		Bukkit.broadcastMessage(CC.translate( "&6[KingOfTheHill]   " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + "   &ehas been controlled by &6" + ((playerFaction == null) ? winner.getName() : (playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]"))));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");

		if (playerFaction != null) {
			int kothPoints = Main.get().getMainConfig().getInt("POINTS.PER_KOTH");

			playerFaction.setPoints(playerFaction.getPoints() + kothPoints);
			playerFaction.setKothCaptures(playerFaction.getKothCaptures() + 1);
			playerFaction.broadcast(CC.translate("&9" + winner.getName() + " &ehas gotten &6" + kothPoints
					+ " &epoint for your faction by capping a KoTH"));
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.get().getMainConfig().getString("EVENT_WIN.KOTH")
				.replace("%PLAYER%", winner.getName()));
	}

	public void handleWinnerCitadel(Player winner) {
		if (this.eventFaction == null) return;

		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());

		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD
				+ this.eventFaction.getEventType().getDisplayName() + ChatColor.GRAY + "] " + ChatColor.GOLD
				+ ((playerFaction == null) ? winner.getName()
						: (playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]"))
				+ ChatColor.YELLOW + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName()
				+ ChatColor.YELLOW + " after " + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true)
				+ ChatColor.YELLOW + " of up-time");

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");

		if (playerFaction != null) {
			int citadelPoints = Main.get().getMainConfig().getInt("POINTS.PER_CITADEL");

			playerFaction.setPoints(playerFaction.getPoints() + citadelPoints);
			playerFaction.broadcast(CC.translate("&9" + winner.getName() + " &ehas gotten &6" + citadelPoints
					+ " &epoint for your faction by capping the Citadel"));
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.get().getMainConfig().getString("EVENT_WIN.CITADEL")
				.replace("%PLAYER%", winner.getName()));
	}

	public void handleWinnerConquest(Player winner) {
		if (this.eventFaction == null) return;

		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner.getUniqueId());
		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.GOLD
				+ this.eventFaction.getEventType().getDisplayName() + ChatColor.GRAY + "] " + ChatColor.GOLD
				+ ((playerFaction == null) ? winner.getName()
				: (playerFaction.getName() + ChatColor.GRAY + " [" + winner.getName() + "]"))
				+ ChatColor.YELLOW + " has captured " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName()
				+ ChatColor.YELLOW + " after " + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true)
				+ ChatColor.YELLOW + " of up-time");

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event cancel");

		if (playerFaction != null) {
			int conquestPoints = Main.get().getMainConfig().getInt("POINTS.PER_CONQUEST");

			playerFaction.setPoints(playerFaction.getPoints() + conquestPoints);
			playerFaction.broadcast(CC.translate("&9" + winner.getName() + " &ehas gotten &6" + conquestPoints
					+ " &epoint for your faction by capping the Conquest"));
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.get().getMainConfig().getString("EVENT_WIN.CONQUEST")
				.replace("%PLAYER%", winner.getName()));
	}

	public boolean tryContesting(EventFaction eventFaction, CommandSender sender) {
		if (this.eventFaction != null) {
			sender.sendMessage(ChatColor.RED + "There is already an active event, use /event cancel to end it.");
			return false;
		}
		if (eventFaction instanceof KothFaction) {
			KothFaction kothFaction = (KothFaction) eventFaction;
			if (kothFaction.getCaptureZone() == null) {
				sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName() + " as its' capture zone is not set.");
				return false;
			}
		}
		if (eventFaction instanceof CitadelFaction) {
			CitadelFaction citadelFaction = (CitadelFaction) eventFaction;
			if (citadelFaction.getCaptureZone() == null) {
				sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName()
						+ " as its' capture zone is not set.");
				return false;
			}
		} else if (eventFaction instanceof ConquestFaction) {
			ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
			Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
			for (ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()) {
				if (!zones.contains(zone)) {
					sender.sendMessage(ChatColor.RED + "Cannot schedule " + eventFaction.getName()
							+ " as capture zone '" + zone.getDisplayName() + ChatColor.RED + "' is not set.");
					return false;
				}
			}
		}
		long millis = System.currentTimeMillis();

		if (this.lastContestedEventMillis + EventTimer.RESCHEDULE_FREEZE_MILLIS - millis > 0L) {
			sender.sendMessage(ChatColor.RED + "Cannot reschedule events within " + EventTimer.RESCHEDULE_FREEZE_WORDS + '.');
			return false;
		}

		this.lastContestedEventMillis = millis;
		this.startStamp = millis;
		this.eventFaction = eventFaction;
		eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
		if (eventFaction instanceof ConquestFaction) {
			this.setRemaining(1000L, true);
			this.setPaused(true);
		}
		Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
		for (CaptureZone captureZone : captureZones) {
			if (captureZone.isActive()) {
				Player player = Iterables.getFirst(captureZone.getCuboid().getPlayers(), null);
				if (player == null) {
					continue;
				}
				if (!eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
					continue;
				}
				captureZone.setCappingPlayer(player);
			}
		}
		eventFaction.setDeathban(true);
		return true;
	}

	public long getUptime() {
		return System.currentTimeMillis() - this.startStamp;
	}

	public long getStartStamp() {
		return this.startStamp;
	}

	private void handleDisconnect(Player player) {
		Preconditions.checkNotNull((Object) player);
		if (this.eventFaction == null) {
			return;
		}
		Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
		for (CaptureZone captureZone : captureZones) {
			if (Objects.equal(captureZone.getCappingPlayer(), player)) {
				this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone,
						this.eventFaction);
				captureZone.setCappingPlayer(null);
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEnderpearl(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile instanceof EnderPearl && (projectile.getShooter()) instanceof Player) {
			this.handleDisconnect((Player) projectile.getShooter());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		this.handleDisconnect(event.getEntity());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLogout(PlayerQuitEvent event) {
		this.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		this.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
		if (this.eventFaction == null) {
			return;
		}
		CaptureZone captureZone = event.getCaptureZone();
		if (!this.eventFaction.getCaptureZones().contains(captureZone)) {
			return;
		}
		Player player = event.getPlayer();
		if (captureZone.getCappingPlayer() == null
				&& this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
			captureZone.setCappingPlayer(player);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
		if (Objects.equal(event.getFaction(), this.eventFaction)) {
			Player player = event.getPlayer();
			CaptureZone captureZone = event.getCaptureZone();
			if (Objects.equal(player, captureZone.getCappingPlayer()) && this.eventFaction
					.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)) {
				captureZone.setCappingPlayer(null);
				for (Player target : captureZone.getCuboid().getPlayers()) {
					if (target != null && !target.equals(player)
							&& this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)) {
						captureZone.setCappingPlayer(target);
						break;
					}
				}
			}
		}
	}

	static {
		RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
		RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords(EventTimer.RESCHEDULE_FREEZE_MILLIS, true, true);
	}
}
