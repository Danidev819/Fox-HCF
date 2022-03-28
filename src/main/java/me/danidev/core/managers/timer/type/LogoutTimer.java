package me.danidev.core.managers.timer.type;

import java.util.UUID;

import me.danidev.core.listeners.CombatLogListener;
import me.danidev.core.managers.timer.PlayerTimer;
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

public class LogoutTimer extends PlayerTimer implements Listener {

    public LogoutTimer() {
        super("Logout", ConfigurationService.LOGOUT_TIMER);
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&c&l");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "You moved a block, Logout" + ChatColor.RED + " timer cancelled.");
            clearCooldown(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.onPlayerMove(event);
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
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (getRemaining(player) > 0L) {
                player.sendMessage(ChatColor.RED + "You were damaged, Logout" + ChatColor.RED + " timer ended.");
                clearCooldown(player);
            }
        }
    }

    @Override
    public void onExpire(UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        CombatLogListener.safelyDisconnect(player);
    }

    public void run(Player player) {
        long remainingMillis = getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage(ChatColor.YELLOW + "Logging out in: " + ChatColor.RED + DurationFormatter.getRemaining(remainingMillis, true));
            player.sendMessage("&cLogout" + ChatColor.YELLOW + " timer is disconnecting you in " + ChatColor.RED + ChatColor.BOLD + DurationFormatter.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}