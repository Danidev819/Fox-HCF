package me.danidev.core.listeners.fixes;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.Listener;

public class HungerFixListener implements Listener {

    public HungerFixListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onFoodLevel(FoodLevelChangeEvent event) {
        if (event.getFoodLevel() < ((Player)event.getEntity()).getFoodLevel() && new Random().nextInt(100) > 4) {
            event.setCancelled(true);
        }
    }
}
