package me.danidev.core.managers.timer.type;

import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.event.FactionClaimChangedEvent;
import me.danidev.core.managers.faction.event.PlayerClaimEnterEvent;
import me.danidev.core.managers.faction.event.cause.ClaimChangeCause;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.faction.type.RoadFaction;
import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.TimerCooldown;
import me.danidev.core.managers.timer.event.TimerClearEvent;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.visualise.VisualType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import com.google.common.base.Optional;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * Timer used to apply PVP Protection to {@link Player}s.
 */
public class ProtectionTimer extends PlayerTimer implements Listener {

	// TODO: Future proof
	private static final String PVP_COMMAND = "/pvp enable";

	// The PlayerPickupItemEvent spams if cancelled, needs a delay between messages to look clean.
	private static final long ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(20L);
	private static final long ITEM_PICKUP_MESSAGE_DELAY = 1250L;
	private static final String ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";
	private Set<UUID> legible;
	private final Map<UUID, Long> itemUUIDPickupDelays = new HashMap<>();
	private final Main plugin;

	public ProtectionTimer(Main plugin) {
		super("PvP TImer", TimeUnit.MINUTES.toMillis(ConfigurationService.PROTECTION_TIMER));
		this.plugin = plugin;
		this.legible = new HashSet<>();
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.translateAlternateColorCodes('&', "&a&l");
	}

	@Override
	public void onExpire(UUID userUUID) {
		Player player = Bukkit.getPlayer(userUUID);
		if (player != null) {
			plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
			player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You no longer have your " + getDisplayName() + ChatColor.RED + ChatColor.BOLD + ", good luck!");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTimerStop(TimerClearEvent event) {
		if (event.getTimer() == this) {
			Optional<UUID> optionalUserUUID = event.getUserUUID();
			if (optionalUserUUID.isPresent()) {
				this.onExpire(optionalUserUUID.get());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClaimChange(FactionClaimChangedEvent event) {
		if (event.getCause() != ClaimChangeCause.CLAIM) {
			return;
		}

		Collection<Claim> claims = event.getAffectedClaims();
		for (Claim claim : claims) {
			Collection<Player> players = claim.getPlayers();
			if (players.isEmpty()) {
				continue;
			}

			Location location = new Location(claim.getWorld(), claim.getMinimumX() - 1, 0, claim.getMinimumZ() - 1);
			location = BukkitUtils.getHighestLocation(location, location);
			for (Player player : players) {
				if (getRemaining(player) > 0L && player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
					//player.sendMessage(ChatColor.RED + "Land was claimed where you were standing. As you still have your " + getName() + " timer, you were teleported away.");
					player.sendMessage(ChatColor.RED + "Land was claimed where you were standing. As you still have your "
							+ this.getName() + " timer, you were teleported away.");
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		World world = player.getWorld();
		Location location = player.getLocation();
		Collection<ItemStack> drops = event.getDrops();
		if (!drops.isEmpty()) {
			Iterator<ItemStack> iterator = drops.iterator();

			// Drop the items manually so we can add meta to prevent
			// PVP Protected players from collecting them.
			long stamp = System.currentTimeMillis() + +ITEM_PICKUP_DELAY;
			while (iterator.hasNext()) {
				itemUUIDPickupDelays.put(world.dropItemNaturally(location, iterator.next()).getUniqueId(), stamp);
				iterator.remove();
			}
		}

		clearCooldown(player);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		long remaining = getRemaining(player);
		if (remaining > 0L) {
			event.setCancelled(false);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		if (player == null)
			return;
		long remaining = getRemaining(player);
		if (remaining > 0L) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot ignite blocks as your PvP Timer"
					+ ChatColor.RED + " timer is active [" + ChatColor.BOLD
					+ DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		long remaining = getRemaining(player);
		if (remaining > 0L) {
			UUID itemUUID = event.getItem().getUniqueId();
			Long delay = itemUUIDPickupDelays.get(itemUUID);
			if (delay == null)
				return;

			// The item has been spawned for over the required pickup time for
			// PVP Protected players, let them pick it up.
			long millis = System.currentTimeMillis();
			if ((delay - millis) > 0L) {
				event.setCancelled(true);

				// Don't let the pickup event spam the player.
				List<MetadataValue> value = player.getMetadata(ITEM_PICKUP_MESSAGE_META_KEY);
				if (value != null && !value.isEmpty() && value.get(0).asLong() - millis <= 0L) {
					player.setMetadata(ITEM_PICKUP_MESSAGE_META_KEY, new FixedMetadataValue(plugin, millis + ITEM_PICKUP_MESSAGE_DELAY));
					player.sendMessage(ChatColor.RED + "You cannot pick this item up for another " + ChatColor.BOLD
							+ DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED
							+ " as your PvP Timer" + ChatColor.RED + " timer is active ["
							+ ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED
							+ " remaining]");
				}
			} else
				itemUUIDPickupDelays.remove(itemUUID);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		TimerCooldown runnable = cooldowns.get(player.getUniqueId());
		if (runnable != null && runnable.getRemaining() > 0L) {
			runnable.setPaused(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore() && !Main.get().isKitMap()) {
			if (!this.plugin.getEotwHandler().isEndOfTheWorld() && this.legible.add(player.getUniqueId())) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&cYou now have PvP Protection since you have died."));
			}
		} else if (this.isPaused(player) && this.getRemaining(player) > 0L
				&& !this.plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()) {
			this.setPaused(player, player.getUniqueId(), false);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerClaimEnterMonitor(PlayerClaimEnterEvent event) {
		Player player = event.getPlayer();
		if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
			this.clearCooldown(player);
			return;
		}
		Faction toFaction = event.getToFaction();
		Faction fromFaction = event.getFromFaction();
		if (fromFaction.isSafezone() && !toFaction.isSafezone()) {
			if (this.legible.remove(player.getUniqueId())) {
				this.setCooldown(player, player.getUniqueId());
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has started."));
				return;
			}
			if (this.getRemaining(player) > 0L) {
				this.setPaused(player, player.getUniqueId(), false);
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has started."));
			}
		} else if (!fromFaction.isSafezone() && toFaction.isSafezone() && this.getRemaining(player) > 0L) {
			player.sendMessage(
					ChatColor.translateAlternateColorCodes('&', "&cYour PvP Protection Timer has been paused."));
			this.setPaused(player, player.getUniqueId(), true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
		Player player = event.getPlayer();
		Faction toFaction = event.getToFaction();
		if (toFaction instanceof ClaimableFaction && (this.getRemaining(player)) > 0L) {
			PlayerFaction playerFaction;
			if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && toFaction instanceof PlayerFaction
					&& (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId())) != null
					&& playerFaction.equals(toFaction)) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&bYou have entered your claim, meaning you no longer have PvP Protection."));
				this.clearCooldown(player);
				return;
			}
			if (!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&cYou cannot enter this claim while you have PvP Protection."));
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player attacker = BukkitUtils.getFinalAttacker(event, true);
			if (attacker == null) {
				return;
			}
			Player player = (Player) entity;
			if (this.getRemaining(player) > 0L) {
				event.setCancelled(true);
				attacker.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&c" + player.getName() + " still has PvP Protection."));
				return;
			}
			if (this.getRemaining(attacker) > 0L) {
				event.setCancelled(true);
				attacker.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&cYou can not attack players while you have PvP Protection. Use &6/pvp enable &cto enable PvP"));
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion potion = event.getPotion();
		if (potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)) {
			for (LivingEntity livingEntity : event.getAffectedEntities()) {
				if (livingEntity instanceof Player) {
					if (getRemaining((Player) livingEntity) > 0L) {
						event.setIntensity(livingEntity, 0);
					}
				}
			}
		}
	}

	@Override
	public long getRemaining(UUID playerUUID) {
		return this.plugin.getEotwHandler().isEndOfTheWorld() ? 0L : super.getRemaining(playerUUID);
	}

	@Override
	public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration,
							   boolean overwrite) {
		return !this.plugin.getEotwHandler().isEndOfTheWorld()
				&& super.setCooldown(player, playerUUID, duration, overwrite);
	}

	public Set<UUID> getLegible() {
		return this.legible;
	}

	private boolean canApply() {
		return !plugin.getEotwHandler().isEndOfTheWorld() && !Main.get().getMainConfig().getBoolean("KITMAP") && plugin.getSotwTimer().getSotwRunnable() == null;
	}
	public boolean hasCooldown(Player shooter) {
		// TODO Auto-generated method stub
		return false;
	}
}