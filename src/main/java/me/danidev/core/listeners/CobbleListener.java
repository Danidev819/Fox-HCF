package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.commands.CobbleCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CobbleListener implements Listener {

    public CobbleListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onPlayerPickup(PlayerPickupItemEvent event) {
        Material material = event.getItem().getItemStack().getType();

        if ((material == Material.STONE || material == Material.COBBLESTONE)
                && CobbleCommand.COBBLE.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
