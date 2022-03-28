package me.danidev.core.listeners.fixes;

import me.danidev.core.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.event.player.PlayerItemDamageEvent;

import org.bukkit.Bukkit;

import java.util.Arrays;
import org.bukkit.Material;
import java.util.List;
import org.bukkit.event.Listener;

public class ArmorFixListener implements Listener {

    private final List<Material> ALLOWED = Arrays.asList(
            Material.GOLD_HELMET,
            Material.GOLD_CHESTPLATE,
            Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
    );
    
    public ArmorFixListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    private void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack != null && ALLOWED.contains(itemStack.getType()) && ThreadLocalRandom.current().nextInt(3) != 0) {
            event.setCancelled(true);
        }
    }
}
