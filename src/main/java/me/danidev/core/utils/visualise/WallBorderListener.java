package me.danidev.core.utils.visualise;

import java.util.List;
import java.util.Iterator;
import java.util.Collection;

import me.danidev.core.Main;
import me.danidev.core.utils.cuboid.Cuboid;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.RoadFaction;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.HashSet;
import com.google.common.base.Predicate;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.event.Listener;

public class WallBorderListener implements Listener {

	private final Main plugin;

	public WallBorderListener(final Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		this.getClass();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		this.getClass();
		final Location now = player.getLocation();
		new BukkitRunnable() {
			public void run() {
				final Location location = player.getLocation();
				if (now.equals((Object) location)) {
					WallBorderListener.this.handlePositionChanged(player, location.getWorld(), location.getBlockX(),
							location.getBlockY(), location.getBlockZ());
				}
			}
		}.runTaskLater((Plugin) this.plugin, 4L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(final PlayerMoveEvent event) {
		this.getClass();
		final Location to = event.getTo();
		final int toX = to.getBlockX();
		final int toY = to.getBlockY();
		final int toZ = to.getBlockZ();
		final Location from = event.getFrom();
		if (from.getBlockX() != toX || from.getBlockY() != toY || from.getBlockZ() != toZ) {
			this.handlePositionChanged(event.getPlayer(), to.getWorld(), toX, toY, toZ);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		this.onPlayerMove((PlayerMoveEvent) event);
	}

	private void handlePositionChanged(final Player player, final World toWorld, final int toX, final int toY,
			final int toZ) {
		VisualType visualType;
		if (this.plugin.getTimerManager().getSpawnTagTimer().getRemaining(player) > 0L) {
			visualType = VisualType.SPAWN_BORDER;
		} else {
			if (this.plugin.getTimerManager().protectionTimer.getRemaining(player) <= 0L) {
				return;
			}
			visualType = VisualType.CLAIM_BORDER;
		}
		this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType,
				(Predicate<VisualBlock>) (visualBlock -> {
					final Location other = visualBlock.getLocation();
					return other.getWorld().equals(toWorld) && (Math.abs(toX - other.getBlockX()) > 7
							|| Math.abs(toY - other.getBlockY()) > 4 || Math.abs(toZ - other.getBlockZ()) > 7);
				}));
		final int minHeight = toY - 3;
		final int maxHeight = toY + 4;
		final int minX = toX - 7;
		final int maxX = toX + 7;
		final int minZ = toZ - 7;
		final int maxZ = toZ + 7;
		final Collection<Claim> added = new HashSet<Claim>();
		for (int x = minX; x < maxX; ++x) {
			for (int z = minZ; z < maxZ; ++z) {
				final Faction faction = this.plugin.getFactionManager().getFactionAt(toWorld, x, z);
				if (faction instanceof ClaimableFaction) {
					if (visualType == VisualType.SPAWN_BORDER) {
						if (!faction.isSafezone()) {
							continue;
						}
					} else if (visualType == VisualType.CLAIM_BORDER) {
						if (faction instanceof RoadFaction) {
							continue;
						}
						if (faction.isSafezone()) {
							continue;
						}
					}
					final Collection<Claim> claims = ((ClaimableFaction) faction).getClaims();
					for (final Claim claim : claims) {
						if (toWorld.equals(claim.getWorld())) {
							added.add(claim);
						}
					}
				}
			}
		}
		if (!added.isEmpty()) {
			final Iterator<Claim> iterator = added.iterator();
			while (iterator.hasNext()) {
				final Claim claim2 = iterator.next();
				final List<Vector> edges = (List<Vector>) claim2.edges();
				for (final Vector edge : edges) {
					if (Math.abs(edge.getBlockX() - toX) > 7) {
						continue;
					}
					if (Math.abs(edge.getBlockZ() - toZ) > 7) {
						continue;
					}
					final Location location = edge.toLocation(toWorld);
					if (location == null) {
						continue;
					}
					final Location first = location.clone();
					first.setY((double) minHeight);
					final Location second = location.clone();
					second.setY((double) maxHeight);
					this.plugin.getVisualiseHandler().generate(player, new Cuboid(first, second), visualType, false)
							.size();
				}
				iterator.remove();
			}
		}
	}

	private static final class WarpTimerRunnable extends BukkitRunnable {
		private WallBorderListener listener;
		private Player player;
		private double lastX;
		private double lastY;
		private double lastZ;

		public WarpTimerRunnable(final WallBorderListener listener, final Player player) {
			this.lastX = Double.MAX_VALUE;
			this.lastY = Double.MAX_VALUE;
			this.lastZ = Double.MAX_VALUE;
			this.listener = listener;
			this.player = player;
		}

		public void run() {
			final Location location = this.player.getLocation();
			final double x = location.getBlockX();
			final double y = location.getBlockY();
			final double z = location.getBlockZ();
			if (this.lastX == x && this.lastY == y && this.lastZ == z) {
				return;
			}
			this.lastX = x;
			this.lastY = y;
			this.lastZ = z;
			this.listener.handlePositionChanged(this.player, this.player.getWorld(), (int) x, (int) y, (int) z);
		}

		public synchronized void cancel() throws IllegalStateException {
			super.cancel();
			this.listener = null;
			this.player = null;
		}
	}
}
