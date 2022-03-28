package me.danidev.core.managers.timer.type;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.LandMap;
import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.TimerCooldown;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class StuckTimer extends PlayerTimer implements Listener {

    public static final int MAX_MOVE_DISTANCE = 5;
    private final Map<UUID, Location> startedLocations = new HashMap<>();
    Main plugin;

    public StuckTimer() {
        super("Stuck", TimeUnit.SECONDS.toMillis(ConfigurationService.STUCK_TIMER), false);

    }

    public String getScoreboardPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&4&l");
    }
    public TimerCooldown clearCooldown(UUID uuid) {
        TimerCooldown runnable = super.clearCooldown(uuid);

        if (runnable != null) {
            this.startedLocations.remove(uuid);
            return runnable;
        }

        return null;
    }

    @Override
    public boolean setCooldown(@Nullable final Player player, final UUID playerUUID, final long millis, final boolean force) {
        if (player != null && super.setCooldown(player, playerUUID, millis, force)) {
            this.startedLocations.put(playerUUID, player.getLocation());
            return true;
        }
        return false;
    }

    private void checkMovement(Player player, Location from, Location to) {
        UUID uuid = player.getUniqueId();
        if (getRemaining(uuid) > 0L) {
            if (from == null) {
                clearCooldown(uuid);
                return;
            }

            int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
            int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
            int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());

            if ((xDiff > 5) || (yDiff > 5) || (zDiff > 5)) {
                this.clearCooldown(uuid);
                player.sendMessage(ChatColor.RED + "You moved more than " + ChatColor.BOLD + 5 + ChatColor.RED + " blocks. Stuck" + ChatColor.RED + " timer ended.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (getRemaining(uuid) > 0L) {
            Location from = this.startedLocations.get(uuid);
            checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (getRemaining(uuid) > 0L) {
            Location from = this.startedLocations.get(uuid);
            checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        final Player player;
        if (entity instanceof Player && this.getRemaining(player = (Player)entity) > 0L) {
            player.sendMessage(ChatColor.RED + "You were damaged, Stuck" + ChatColor.RED + " timer ended.");
            this.clearCooldown(player);
        }
    }

    public void onExpire(UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        final Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), 124);
        if (nearest == null) {
            player.sendMessage(ChatColor.RED + "No safe-location found.");
            return;
        }
        if (player.teleport(nearest, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            player.sendMessage(ChatColor.YELLOW + "Stuck" + ChatColor.YELLOW + " timer has teleported you to the nearest safe area.");
        }
    }

    public void run(Player player) {
        final long remainingMillis = this.getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage(ChatColor.BLUE + "Stuck timer is teleporting you in " + ChatColor.BOLD + DurationFormatter.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}