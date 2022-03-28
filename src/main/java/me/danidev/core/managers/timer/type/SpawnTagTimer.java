package me.danidev.core.managers.timer.type;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.event.PlayerClaimEnterEvent;
import me.danidev.core.managers.faction.event.PlayerJoinFactionEvent;
import me.danidev.core.managers.faction.event.PlayerLeaveFactionEvent;
import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.event.TimerClearEvent;
import me.danidev.core.managers.timer.event.TimerStartEvent;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.visualise.VisualType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import com.google.common.base.Optional;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.inventory.ItemStack;

/**
 * Timer used to tag {@link Player}s in combat to prevent entering safe-zones.
 */
public class SpawnTagTimer extends PlayerTimer implements Listener {

	private final Main plugin;

	public SpawnTagTimer(Main plugin) {
		super("Spawn Tag", TimeUnit.SECONDS.toMillis(ConfigurationService.SPAWN_TAG_TIMER));
		this.plugin = plugin;
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.translateAlternateColorCodes('&', "&c&l");
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

	@Override
	public void onExpire(UUID userUUID) {
		Player player = Bukkit.getPlayer(userUUID);
		if (player != null) {
			plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionJoin(PlayerJoinFactionEvent event) {
		Optional<Player> optional = event.getPlayer();
		if (optional.isPresent()) {
			Player player = optional.get();
			long remaining = getRemaining(player);
			if (remaining > 0L) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot join factions whilst your Spawn Tag"
						+ ChatColor.RED + " timer is active [" + ChatColor.BOLD
						+ DurationFormatter.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionLeave(PlayerLeaveFactionEvent event) {
		final Optional<Player> optional = event.getPlayer();
		final Player player;
		if (optional.isPresent() && this.getRemaining(player = (Player) optional.get()) > 0L) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot join factions whilst your Spawn Tag"
					+ ChatColor.RED + " timer is active [" + ChatColor.BOLD
					+ DurationFormatter.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPreventClaimEnter(PlayerClaimEnterEvent event) {
		if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
			return;
		}
		final Player player = event.getPlayer();
		if (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()
				&& this.getRemaining(player) > 0L) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot enter "
					+ event.getToFaction().getDisplayName(player) + ChatColor.RED + " whilst your Spawn Tag"
					+ ChatColor.RED + " timer is active [" + ChatColor.BOLD
					+ DurationFormatter.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
		}
	}

	private static final long NON_WEAPON_TAG = 5000L;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		final Player attacker = BukkitUtils.getFinalAttacker(event, true);
		final Entity entity;
		if (attacker != null && (entity = event.getEntity()) instanceof Player) {
			final Player attacked = (Player) entity;
			boolean weapon = event.getDamager() instanceof Arrow;
			if (!weapon) {
				ItemStack stack = attacker.getItemInHand();
				weapon = (stack != null && EnchantmentTarget.WEAPON.includes(stack));
			}
			final long duration = weapon ? this.defaultCooldown : 30000L;
			this.setCooldown(attacked, attacked.getUniqueId(), Math.max(this.getRemaining(attacked), duration), true);
			this.setCooldown(attacker, attacker.getUniqueId(), Math.max(this.getRemaining(attacker), duration), true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTimerStart(TimerStartEvent event) {
		final Optional<Player> optional;
		if (event.getTimer().equals(this) && (optional = event.getPlayer()).isPresent()) {
			final Player player = optional.get();
			player.sendMessage(ChatColor.YELLOW + "You are now spawn-tagged for " + ChatColor.RED
					+ DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.YELLOW
					+ '.');
			return;
			}
		}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		clearCooldown(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPreventClaimEnterMonitor(PlayerClaimEnterEvent event) {
		if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && !event.getFromFaction().isSafezone()
				&& event.getToFaction().isSafezone()) {
			this.clearCooldown(event.getPlayer());
		}
	}
}
