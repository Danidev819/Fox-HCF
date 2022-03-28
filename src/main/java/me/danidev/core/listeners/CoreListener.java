package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class CoreListener implements Listener {

	public CoreListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerJoinMessage(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		if (Main.get().getMainConfig().getBoolean("JOIN_MESSAGE.ENABLE")) {
			Player player = event.getPlayer();

			Main.get().getMainConfig().getStringList("JOIN_MESSAGE.MESSAGE").forEach(message ->
					player.sendMessage(CC.translate(message.replace("%PLAYER%", player.getName()))));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		Main.get().getUserManager().getBaseUser(player.getUniqueId()).setBackLocation(player.getLocation().clone());

		if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
			event.getDrops().clear();
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE && !event.getPlayer().isOp()) {
			event.setCancelled(true);
		}

		if (Main.get().isKitMap()) {
			Faction factionAt = Main.get().getFactionManager().getFactionAt(event.getPlayer().getLocation());

			if (factionAt.isSafezone()) {
				event.setCancelled(true);
				event.getPlayer().updateInventory();
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			Main.get().getUserManager().getBaseUser(event.getPlayer().getUniqueId())
					.setBackLocation(event.getFrom().clone());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

		if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;

		Location location = event.getLocation();
		Faction factionAt = Main.get().getFactionManager().getFactionAt(location);

		if (factionAt.isSafezone() && reason == CreatureSpawnEvent.SpawnReason.SPAWNER) return;

		if (factionAt.isSafezone() && reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
			event.setCancelled(true);
		}
		if (reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLightning(BlockIgniteEvent event) {
		if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerKickEvent event) {
		event.setLeaveMessage(null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Player player = event.getPlayer();

		Main.get().getVisualiseHandler().clearVisualBlocks(player, null, null, false);
		Main.get().getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		Main.get().getVisualiseHandler().clearVisualBlocks(player, null, null, false);
		Main.get().getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onBreakBlock(BlockBreakEvent event) {
		if (Main.get().getMainConfig().getBoolean("INVENTORY.ENABLE")) {
			Player player = event.getPlayer();

			if (event.isCancelled()) return;

			if (player.getGameMode() != GameMode.SURVIVAL) return;

			if (player.getInventory().firstEmpty() < 0) return;

			if (Main.get().getMainConfig().getStringList("INVENTORY.BLOCKS").contains(event.getBlock().getType().name())) {
				ItemStack itemStack = new ItemBuilder(event.getBlock().getType())
						.build();

				player.getInventory().addItem(itemStack);
				event.getBlock().setType(Material.AIR);
			}
		}
	}
}
