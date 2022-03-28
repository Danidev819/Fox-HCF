package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Utils;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Squid;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.material.EnderChest;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.Material;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;

import org.bukkit.event.Listener;

public class WorldListener implements Listener {

    public WorldListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER
                && event.getBlock().getState() instanceof CreatureSpawner
                && !player.hasPermission("fhcf.faction.protection.bypass")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not break spawners in the nether.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER
                && event.getBlock().getState() instanceof CreatureSpawner
                && !player.hasPermission("fhcf.faction.protection.bypass")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not place spawners in the nether.");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockChange(BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPortalEnter(EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBedEnter(PlayerBedEnterEvent event) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWitherChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @SuppressWarnings("incomplete-switch")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            Main.get().getEconomyManager().addBalance(player.getUniqueId(), Main.get().getMainConfig().getInt("JOIN_BALANCE"));
            event.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Squid) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeave(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END
                && (event.getPlayer().getLocation().getBlock().getType() == Material.WATER
                || event.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER)) {

            Location spawn = Utils.destringifyLocation(Main.get().getLocationsConfig().getString("END_EXIT"));

            if (spawn == null) {
                player.sendMessage(CC.translate("&cYou do not created endexit!."));
                return;
            }
            event.getPlayer().teleport(spawn);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        Player killer = event.getEntity().getKiller();

        if (Main.get().isKitMap() && event.getEntity().getKiller() != null && killer != player) {
            int balanceReward = Main.get().getMainConfig().getInt("BALANCE_REWARD");

            Main.get().getEconomyManager().addBalance(killer.getUniqueId(), balanceReward);
            killer.sendMessage(CC.translate("&eYou have gained &a$" + balanceReward + " &efor killing &c" + event.getEntity().getName() + "&e."));
        }
    }
}
