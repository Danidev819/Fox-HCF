package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.cuboid.Cuboid;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.faction.CapturableFaction;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Hanging;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Cauldron;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Horse;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.TravelAgent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerPortalEvent;

import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import org.bukkit.Bukkit;
import java.util.Objects;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.World;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import me.danidev.core.managers.faction.event.CaptureZoneEnterEvent;
import me.danidev.core.managers.faction.event.CaptureZoneLeaveEvent;
import me.danidev.core.managers.faction.event.PlayerClaimEnterEvent;
import me.danidev.core.managers.faction.struct.Raidable;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.GlowstoneFaction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.faction.type.WarzoneFaction;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class FactionsCoreListener implements Listener {

	private final ImmutableMultimap<Object, Object> ITEMS_ALLOWED = ImmutableMultimap.builder().put(Material.DIAMOND_HOE, Material.GRASS)
			.put(Material.GOLD_HOE, Material.GRASS).put(Material.IRON_HOE, Material.GRASS)
			.put(Material.STONE_HOE, Material.GRASS).put(Material.WOOD_HOE, Material.GRASS)
			.build();

	private final ImmutableSet<Material> BLOCKS_ALLOWED = Sets.immutableEnumSet(Material.BED, Material.BED_BLOCK, Material.BEACON,
			Material.FENCE_GATE, Material.IRON_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR,
			Material.IRON_DOOR_BLOCK, Material.CHEST, Material.TRAPPED_CHEST, Material.BREWING_STAND,
			Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.STONE_BUTTON, Material.WOOD_BUTTON,
			Material.ENCHANTMENT_TABLE, Material.ANVIL, Material.LEVER, Material.FIRE, Material.FURNACE,
			Material.BURNING_FURNACE, Material.REDSTONE_COMPARATOR, Material.REDSTONE_COMPARATOR_ON,
			Material.REDSTONE_COMPARATOR_OFF, Material.DIODE, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF);

	public FactionsCoreListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static boolean build(Entity entity, Location location, String denyMessage) {
		return attemptBuild(entity, location, denyMessage, false);
	}

	public static boolean attemptBuild(Entity entity, Location location, String denyMessage, boolean isInteraction) {
		boolean result = false;

		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (player.getGameMode() == GameMode.CREATIVE) {
				return true;
			}

			if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
				player.sendMessage(CC.translate("&cYou can't build in the End."));
				return false;
			}

			Faction factionAt = Main.get().getFactionManager().getFactionAt(location);

			if (!(factionAt instanceof ClaimableFaction)) {
				result = true;
			}
			else if (factionAt instanceof Raidable && ((Raidable) factionAt).isRaidable()) {
				result = true;
			}

			PlayerFaction playerFaction;

			if (factionAt instanceof PlayerFaction
					&& (playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId())) != null
					&& playerFaction.equals(factionAt)) {
				result = true;
			}
			if (result) {
				int build_radius = ConfigurationService.BUILD_RADIUS;

				if (!isInteraction && Math.abs(location.getBlockX()) <= build_radius && Math.abs(location.getBlockZ()) <= build_radius) {
					if (denyMessage != null) {
						player.sendMessage(CC.translate("&cYou cannot build within " + build_radius + " blocks of spawn."));
					}
					return false;
				}
			}
			else if (denyMessage != null) {
				player.sendMessage(String.format(denyMessage, factionAt.getDisplayName(player)));
			}
		}
		return result;
	}

	public static boolean canBuildAt(Location from, Location to) {
		Faction toFactionAt = Main.get().getFactionManager().getFactionAt(to);

		return !(toFactionAt instanceof Raidable)
				|| ((Raidable) toFactionAt).isRaidable()
				|| toFactionAt.equals(Main.get().getFactionManager().getFactionAt(from));
	}

	private void handleMove(PlayerMoveEvent event, PlayerClaimEnterEvent.EnterCause enterCause) {
		Location from = event.getFrom();
		Location to = event.getTo();

		if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

		Player player = event.getPlayer();

		boolean cancelled = false;

		Faction fromFaction = Main.get().getFactionManager().getFactionAt(from);
		Faction toFaction;

		if (!Objects.equals(fromFaction, toFaction = Main.get().getFactionManager().getFactionAt(to))) {

			PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
			Bukkit.getPluginManager().callEvent(calledEvent);
			cancelled = calledEvent.isCancelled();
		}
		else if (toFaction instanceof CapturableFaction) {
			CapturableFaction capturableFaction = (CapturableFaction) toFaction;
			for (CaptureZone captureZone : capturableFaction.getCaptureZones()) {
				Cuboid cuboid = captureZone.getCuboid();

				if (cuboid == null) continue;

				boolean containsFrom = cuboid.contains(from);
				boolean containsTo = cuboid.contains(to);

				if (containsFrom && !containsTo) {

					CaptureZoneLeaveEvent calledEvent2 = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
					Bukkit.getPluginManager().callEvent(calledEvent2);
					cancelled = calledEvent2.isCancelled();
					break;
				}

				if (containsFrom) continue;

				if (!containsTo) continue;

				if (player.getGameMode() == GameMode.CREATIVE) return;

				CaptureZoneEnterEvent calledEvent3 = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
				Bukkit.getPluginManager().callEvent(calledEvent3);
				cancelled = calledEvent3.isCancelled();
				break;
			}
		}
		if (cancelled) {
			if (enterCause == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
				event.setCancelled(true);
			}
			else {
				from.add(0.5, 0.0, 0.5);
				event.setTo(from);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
			this.handleMove(event, PlayerClaimEnterEvent.EnterCause.MOVEMENT);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerTeleportEvent event) {
		this.handleMove(event, PlayerClaimEnterEvent.EnterCause.TELEPORT);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event) {
		Faction factionAt = Main.get().getFactionManager().getFactionAt(event.getBlock().getLocation());

		if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onStickyPistonExtend(BlockPistonExtendEvent event) {
		Block block = event.getBlock();
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
		Faction targetFaction;

		if ((targetBlock.isEmpty() || targetBlock.isLiquid())
				&& (targetFaction = Main.get().getFactionManager().getFactionAt(targetBlock.getLocation())) instanceof Raidable
				&& !((Raidable) targetFaction).isRaidable()
				&& !targetFaction.equals(Main.get().getFactionManager().getFactionAt(block))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onStickyPistonRetract(BlockPistonRetractEvent event) {
		if (!event.isSticky()) return;

		Location retractLocation = event.getRetractLocation();
		Block retractBlock = retractLocation.getBlock();

		if (!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
			Block block = event.getBlock();
			Faction targetFaction = Main.get().getFactionManager().getFactionAt(retractLocation);

			if (targetFaction instanceof Raidable
					&& !((Raidable) targetFaction).isRaidable()
					&& !targetFaction.equals(Main.get().getFactionManager().getFactionAt(block))) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockFromTo(BlockFromToEvent event) {
		Block toBlock = event.getToBlock();
		Block fromBlock = event.getBlock();

		Material fromType = fromBlock.getType();
		Material toType = toBlock.getType();

		if ((toType == Material.REDSTONE_WIRE
				|| toType == Material.TRIPWIRE) && (fromType == Material.AIR
				|| fromType == Material.STATIONARY_LAVA
				|| fromType == Material.LAVA)) {
			toBlock.setType(Material.AIR);
		}
		if ((toBlock.getType() == Material.WATER || toBlock.getType() == Material.STATIONARY_WATER
				|| toBlock.getType() == Material.LAVA || toBlock.getType() == Material.STATIONARY_LAVA)
				&& !canBuildAt(fromBlock.getLocation(), toBlock.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
				&& Main.get().getFactionManager().getFactionAt(event.getTo()).isSafezone()
				&& !Main.get().getFactionManager().getFactionAt(event.getFrom()).isSafezone()) {

			Player player = event.getPlayer();
			Main.get().getTimerManager().enderPearlTimer.refund(player);
			player.sendMessage(CC.translate("&cYou cannot Enderpearl into safe-zones, used Enderpearl has been refunded."));
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPalacePearl(PlayerTeleportEvent event) {
		Player shooter = event.getPlayer();
		Location entityLoc = shooter.getLocation().clone();
		Faction factionAt = Main.get().getFactionManager().getFactionAt(entityLoc);

		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && factionAt instanceof CitadelFaction) {
			Player player = event.getPlayer();
			Main.get().getTimerManager().enderPearlTimer.refund(player);
			player.sendMessage(CC.translate("&cYou cannot Enderpearl into Citadel, used Enderpearl has been refunded."));
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
			Location from = event.getFrom();
			Location to = event.getTo();

			Player player = event.getPlayer();
			Faction fromFac = Main.get().getFactionManager().getFactionAt(from);

			if (fromFac.isSafezone()) {
				event.setTo(to.getWorld().getSpawnLocation().add(0.5, 0.0, 0.5));
				event.useTravelAgent(false);
				return;
			}
			if (event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
				TravelAgent travelAgent = event.getPortalTravelAgent();

				if (!travelAgent.getCanCreatePortal()) return;

				Location foundPortal = travelAgent.findPortal(to);

				if (foundPortal != null) return;

				Faction factionAt = Main.get().getFactionManager().getFactionAt(to);

				if (factionAt instanceof ClaimableFaction) {
					PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

					if (playerFaction != null && playerFaction.equals(factionAt)) return;

					player.sendMessage(ChatColor.YELLOW + "Portal would have created portal in territory of "
							+ factionAt.getDisplayName(player) + ChatColor.YELLOW + '.');
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

		if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;

		Location location = event.getLocation();
		Faction factionAt = Main.get().getFactionManager().getFactionAt(location);

		if (factionAt.isSafezone() && reason == CreatureSpawnEvent.SpawnReason.SPAWNER) return;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player) {
			Player player = (Player) entity;
			Faction playerFactionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());
			EntityDamageEvent.DamageCause cause = event.getCause();

			if (playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE) {
				event.setCancelled(true);
			}

			Player attacker;

			if ((attacker = BukkitUtils.getFinalAttacker(event, true)) != null) {
				Faction attackerFactionAt = Main.get().getFactionManager().getFactionAt(attacker.getLocation());

				if (attackerFactionAt.isSafezone()) {
					event.setCancelled(true);
					attacker.sendMessage(CC.translate("&cYou cannot attack players in safezone."));
					return;
				}

				if (playerFactionAt.isSafezone()) {
					attacker.sendMessage(CC.translate("&cYou cannot attack players in safezone."));
					return;
				}

				PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());
				PlayerFaction attackerFaction = Main.get().getFactionManager().getPlayerFaction(attacker.getUniqueId());

				if (playerFaction != null && attackerFaction != null) {
					if (attackerFaction.equals(playerFaction)) {
						if (playerFaction.isFriendlyFire()) {
							event.setCancelled(false);
							return;
						}
						attacker.sendMessage(CC.translate("&a" + player.getName() + " &eis in your faction."));
						event.setCancelled(true);
					}
					else if (attackerFaction.getAllied().contains(playerFaction.getUniqueID())) {
						attacker.sendMessage(CC.translate("&9" + player.getName() + " &eis your ally."));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onVehicleEnter(VehicleEnterEvent event) {
		Entity entered = event.getEntered();
		AnimalTamer owner;
		if (entered instanceof Player && event.getVehicle() instanceof Horse
				&& (owner = ((Horse) event.getVehicle()).getOwner()) != null && !owner.equals(entered)) {
			((Player) entered).sendMessage(ChatColor.YELLOW + "You cannot enter a Horse that belongs to "
					+ ChatColor.RED + owner.getName() + ChatColor.YELLOW + '.');
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion potion = event.getEntity();

		if (!BukkitUtils.isDebuff(potion)) return;

		Faction factionAt = Main.get().getFactionManager().getFactionAt(potion.getLocation());

		if (factionAt.isSafezone()) {
			event.setCancelled(true);
			return;
		}

		ProjectileSource source = potion.getShooter();

		if (source instanceof Player) {
			Player player = (Player) source;

			for (LivingEntity affected : event.getAffectedEntities()) {
				Player target;

				if (affected instanceof Player && !player.equals(affected) && !(target = (Player) affected).equals(source)) {
					if (!Main.get().getFactionManager().getFactionAt(target.getLocation()).isSafezone()) {
						continue;
					}
					event.setIntensity(affected, 0.0);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityTarget(EntityTargetEvent event) {
		switch (event.getReason()) {
		case CLOSEST_PLAYER:
		case RANDOM_TARGET: {
			Entity target = event.getTarget();
			if (event.getEntity() instanceof LivingEntity && target instanceof Player) {
				Faction factionAt;
				if (!(factionAt = Main.get().getFactionManager().getFactionAt(target.getLocation())).isSafezone()) {
					PlayerFaction playerFaction;
					if ((playerFaction = Main.get().getFactionManager().getPlayerFaction(target.getUniqueId())) == null) {
						break;
					}
					if (!factionAt.equals(playerFaction)) {
						break;
					}
				}
				event.setCancelled(true);
				break;
			}
			break;
		}
		default:
			break;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasBlock()) return;

		Block block = event.getClickedBlock();
		Action action = event.getAction();

		if (action == Action.PHYSICAL && !build(event.getPlayer(), block.getLocation(), null)) {
			event.setCancelled(true);
		}
		if (action == Action.RIGHT_CLICK_BLOCK) {
			boolean canBuild = !BLOCKS_ALLOWED.contains(block.getType());

			if (canBuild) {
				Material itemType = event.hasItem() ? event.getItem().getType() : null;

				if (itemType != null && ITEMS_ALLOWED.containsKey(itemType)
						&& ITEMS_ALLOWED.get(itemType).contains(event.getClickedBlock().getType())) {
					canBuild = false;
				}
				else {
					MaterialData materialData = block.getState().getData();

					if (materialData instanceof Cauldron
							&& !((Cauldron) materialData).isEmpty()
							&& event.hasItem() && event.getItem().getType() == Material.GLASS_BOTTLE) {
						canBuild = false;
					}
				}
			}
			if (!block.getType().equals(Material.WORKBENCH)
					&& !canBuild
					&& !attemptBuild(event.getPlayer(), block.getLocation(), ChatColor.YELLOW
					+ "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.', true)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBurn(BlockBurnEvent event) {
		Faction factionAt = Main.get().getFactionManager().getFactionAt(event.getBlock().getLocation());

		if (factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable) factionAt).isRaidable())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockFade(BlockFadeEvent event) {
		Faction factionAt = Main.get().getFactionManager().getFactionAt(event.getBlock().getLocation());

		if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLeavesDelay(LeavesDecayEvent event) {
		Faction factionAt = Main.get().getFactionManager().getFactionAt(event.getBlock().getLocation());

		if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockForm(BlockFormEvent event) {
		Faction factionAt = Main.get().getFactionManager().getFactionAt(event.getBlock().getLocation());

		if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof LivingEntity && !build(entity, event.getBlock().getLocation(), null)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Faction factionAt = Main.get().getFactionManager().getFactionAt(event.getBlock().getLocation());

		if (factionAt instanceof GlowstoneFaction && event.getBlock().getType() == Material.GLOWSTONE) return;

		if (!build(event.getPlayer(), event.getBlock().getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!build(event.getPlayer(), event.getBlockPlaced().getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketFill(PlayerBucketFillEvent event) {
		if (!build(event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!build(event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();

		if (remover instanceof Player && !build(remover, event.getEntity().getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingPlace(HangingPlaceEvent event) {
		if (!build(event.getPlayer(), event.getEntity().getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Hanging
				&& !build(BukkitUtils.getFinalAttacker(event, false), entity.getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onHangingInteractByPlayer(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();

		if (entity instanceof Hanging && !build(event.getPlayer(), entity.getLocation(), ChatColor.YELLOW
				+ "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}
}
