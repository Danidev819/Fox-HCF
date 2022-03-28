package me.danidev.core.listeners.fixes;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.projectiles.ProjectileSource;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.bukkit.event.Listener;

public class PotionLimitListener implements Listener {

    public PotionLimitListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onSplash(PotionSplashEvent event) {
        if (this.isPotionDisabled(event.getPotion().getItem())) {
            event.setCancelled(true);

            ProjectileSource projectileSource = event.getEntity().getShooter();

            if (projectileSource instanceof Player) {
                Player player = (Player) projectileSource;

                player.sendMessage(CC.translate("&cYou can't use this potion."));
                player.setItemInHand(null);
            }
        }
    }
    
    @EventHandler
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (this.isPotionDisabled(event.getItem())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(CC.translate("&cYou can't use this potion."));
            event.getPlayer().setItemInHand(null);
        }
    }
    
    @EventHandler
    private void onBrew(BrewEvent event) {
        BrewerInventory brewerInventory = event.getContents();
        BrewingStand brewingStand = brewerInventory.getHolder();

        brewingStand.setBrewingTime(200);

        if (this.isPotionDisabled(event.getContents().getItem(0))) {
            event.setCancelled(true);
        }
    }

    private boolean isPotionDisabled(ItemStack itemStack) {
        return itemStack.getType() == Material.POTION && ConfigurationService.POTIONS_DISABLED.contains(itemStack.getDurability());
    }
}
