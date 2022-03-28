package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.combatlog.CombatLogEntry;
import me.danidev.core.managers.combatlog.LoggerDeathEvent;
import me.danidev.core.managers.combatlog.LoggerEntity;
import me.danidev.core.managers.combatlog.LoggerSpawnEvent;
import me.danidev.core.utils.PlayerUtil;
import me.danidev.core.utils.others.InventoryUtils;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Bukkit;

import org.bukkit.GameMode;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import java.util.Collection;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.OfflinePlayer;
import java.util.Iterator;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Set;

import org.bukkit.event.Listener;

public class CombatLogListener implements Listener {

	private static final int NEARBY_SPAWN_RADIUS = 64;
	private static final Set<UUID> SAFE_DISCONNECTS;
	private static final Map<UUID, CombatLogEntry> LOGGERS;

	static {
		SAFE_DISCONNECTS = new HashSet<>();
		LOGGERS = new HashMap<>();
	}

	public CombatLogListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static void safelyDisconnect(Player player) {
		if (CombatLogListener.SAFE_DISCONNECTS.add(player.getUniqueId())) {
			PlayerUtil.sendToHub(player);
		}
	}

	public static void removeCombatLoggers() {
		Iterator<CombatLogEntry> iterator = CombatLogListener.LOGGERS.values().iterator();
		while (iterator.hasNext()) {
			CombatLogEntry entry = iterator.next();
			entry.task.cancel();
			entry.loggerEntity.getBukkitEntity().remove();
			iterator.remove();
		}
		CombatLogListener.SAFE_DISCONNECTS.clear();
	}

	public static void removeCombatLogger(OfflinePlayer player) {
		CombatLogEntry entry = CombatLogListener.LOGGERS.get(player.getUniqueId());
		entry.task.cancel();
		entry.loggerEntity.getBukkitEntity().remove();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuitSafe(PlayerQuitEvent event) {
		CombatLogListener.SAFE_DISCONNECTS.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLoggerInteract(EntityInteractEvent event) {
		Collection<CombatLogEntry> entries = CombatLogListener.LOGGERS.values();
		for (CombatLogEntry entry : entries) {
			if (entry.loggerEntity.getBukkitEntity().equals(event.getEntity())) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLoggerDeath(LoggerDeathEvent event) {
		CombatLogEntry entry = CombatLogListener.LOGGERS.remove(event.getLoggerEntity().getPlayerUUID());
		if (entry != null) {
			entry.task.cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		CombatLogEntry combatLogEntry = CombatLogListener.LOGGERS.remove(event.getPlayer().getUniqueId());
		if (combatLogEntry != null) {
			CraftLivingEntity loggerEntity = combatLogEntry.loggerEntity.getBukkitEntity();
			Player player = event.getPlayer();
			event.setSpawnLocation(loggerEntity.getLocation());
			player.setFallDistance(loggerEntity.getFallDistance());
			player.setRemainingAir(loggerEntity.getRemainingAir());
			loggerEntity.remove();
			combatLogEntry.task.cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerInventory inventory = player.getInventory();
		if (player.getGameMode() != GameMode.CREATIVE && !player.isDead()
				&& !CombatLogListener.SAFE_DISCONNECTS.contains(uuid)) {
			if (InventoryUtils.isEmpty(inventory)
					|| Main.get().getTimerManager().protectionTimer.getRemaining(uuid) > 0L) {
				return;
			}
			if (Main.get().getTimerManager().getTeleportTimer().getNearbyEnemies(player, NEARBY_SPAWN_RADIUS) <= 0) {
				return;
			}
			Location location = player.getLocation();

			if (Main.get().getFactionManager().getFactionAt(location).isSafezone()) {
				return;
			}
			if (CombatLogListener.LOGGERS.containsKey(player.getUniqueId())) {
				return;
			}

			World world = location.getWorld();
			LoggerEntity loggerEntity = new LoggerEntity(world, location, player);
			LoggerSpawnEvent calledEvent = new LoggerSpawnEvent(loggerEntity);
			Bukkit.getPluginManager().callEvent(calledEvent);
			CombatLogListener.LOGGERS.put(uuid, new CombatLogEntry(loggerEntity, new LoggerRemovable(uuid, loggerEntity).runTaskLater(Main.get(), 600L)));
			CraftEntity craftEntity = loggerEntity.getBukkitEntity();
			if (craftEntity != null) {
				CraftLivingEntity craftLivingEntity = (CraftLivingEntity) craftEntity;
				EntityEquipment entityEquipment = craftLivingEntity.getEquipment();
				entityEquipment.setItemInHand(inventory.getItemInHand());
				entityEquipment.setArmorContents(inventory.getArmorContents());
			}
		}
	}

	private static class LoggerRemovable extends BukkitRunnable {
		private UUID uuid;
		private LoggerEntity loggerEntity;

		public LoggerRemovable(UUID uuid, LoggerEntity loggerEntity) {
			this.uuid = uuid;
			this.loggerEntity = loggerEntity;
		}

		public void run() {
			if (CombatLogListener.LOGGERS.remove(this.uuid) != null) {
				this.loggerEntity.dead = true;
			}
		}
	}
}
