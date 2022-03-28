package me.danidev.core.listeners;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Creeper;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.Listener;

public class CreeperFriendlyListener implements Listener {

    public CreeperFriendlyListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onTargetEvent(EntityTargetEvent e) {
        if (e.getEntity() instanceof Creeper) {
            e.setCancelled(true);
        }
    }
}
