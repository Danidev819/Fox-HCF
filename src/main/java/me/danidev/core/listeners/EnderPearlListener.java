package me.danidev.core.listeners;

import java.util.Map;
import java.util.UUID;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import net.minecraft.util.com.google.common.collect.Maps;

public class EnderPearlListener implements Listener {

    private final Map<UUID, EnderPearl> thrownPearlsMap;

    public EnderPearlListener() {
        this.thrownPearlsMap = Maps.newConcurrentMap();

        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.get(), () -> {
            for (Map.Entry<UUID, EnderPearl> pearlsEntry : this.thrownPearlsMap.entrySet()) {
                UUID uuid = pearlsEntry.getKey();
                EnderPearl pearl = pearlsEntry.getValue();

                if (pearl.isDead()) {
                    this.thrownPearlsMap.remove(uuid);
                }
            }
        }, 20L, 20L);

        Bukkit.getPluginManager().registerEvents(this, Main.get());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntityType() != EntityType.ENDER_PEARL) {
            return;
        }

        EnderPearl enderpearl = (EnderPearl) event.getEntity();

        if (!(enderpearl.getShooter() instanceof Player)) {
            return;
        }

        this.thrownPearlsMap.put(((Player) enderpearl.getShooter()).getUniqueId(), enderpearl);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!this.thrownPearlsMap.containsKey(player.getUniqueId())) {
            return;
        }

        this.thrownPearlsMap.get(player.getUniqueId()).remove();
        this.thrownPearlsMap.remove(player.getUniqueId());
    }
}
