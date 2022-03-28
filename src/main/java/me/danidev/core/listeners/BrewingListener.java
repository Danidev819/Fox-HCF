package me.danidev.core.listeners;

import java.util.Iterator;

import me.danidev.core.Main;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.event.EventHandler;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Bukkit;
import java.util.HashMap;

import org.bukkit.block.BrewingStand;
import org.bukkit.Location;
import java.util.Map;
import org.bukkit.event.Listener;

public class BrewingListener implements Listener {

    private final Map<Location, BrewingStand> activeStands;
    
    public BrewingListener(Main plugin) {
        this.activeStands = new HashMap<>();
        new BrewingUpdateTask().runTaskTimer(plugin, 1L, 1L);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            BlockState state = event.getClickedBlock().getState();
            if (state instanceof BrewingStand) {
                BrewingStand brewingStand = (BrewingStand) state;
                this.activeStands.put(brewingStand.getLocation(), brewingStand);
            }
        }
    }
    
    public class BrewingUpdateTask extends BukkitRunnable
    {
        public void run() {
            if (BrewingListener.this.activeStands.isEmpty()) {
                return;
            }
            Iterator<Map.Entry<Location, BrewingStand>> standLoc = BrewingListener.this.activeStands.entrySet().iterator();
            while (standLoc.hasNext()) {
                BrewingStand stand = standLoc.next().getValue();
                if (!stand.getChunk().isLoaded()) {
                    standLoc.remove();
                }
                else {
                    if (stand.getBrewingTime() <= 1) {
                        continue;
                    }
                    stand.setBrewingTime(Math.max(1, stand.getBrewingTime() - 2));
                }
            }
        }
    }
}
