package me.danidev.core.listeners;

import lombok.Getter;
import lombok.Setter;
import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.timer.type.sotw.SOTWCommand;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EndListener implements Listener {


    public static boolean endActive = Main.get().getConfig().getBoolean("END-ENABLED");
    @Getter
    @Setter
    private static Location endReturn;

    private final Map<String, Long> msgCooldown = new HashMap<>();

    public EndListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            Faction team = Main.get().getFactionManager().getFaction(event.getEntity().getKiller().getUniqueId());
            String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

            if (team != null) {
                teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
            }

            for (int i = 0; i < 6; i++) {
                Main.get().getServer().broadcastMessage("");
            }

            Main.get().getServer().broadcastMessage(ChatColor.BLACK + "████████");
            Main.get().getServer().broadcastMessage(ChatColor.BLACK + "████████");
            Main.get().getServer().broadcastMessage(ChatColor.BLACK + "████████" + ChatColor.GOLD + " [Enderdragon]");
            Main.get().getServer().broadcastMessage(ChatColor.BLACK + "████████" + ChatColor.YELLOW + " killed by");
            Main.get().getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.LIGHT_PURPLE + "█" + ChatColor.BLACK + "██" + ChatColor.LIGHT_PURPLE + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.LIGHT_PURPLE + "█" + " " + teamName);
            Main.get().getServer().broadcastMessage(ChatColor.BLACK + "████████" + " " + event.getEntity().getKiller().getDisplayName());
            Main.get().getServer().broadcastMessage(ChatColor.BLACK + "████████");
            Main.get().getServer().broadcastMessage(ChatColor.BLACK + "████████");

            ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
            ItemMeta itemMeta = dragonEgg.getItemMeta();
            DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

            itemMeta.setLore(Arrays.asList
                    (ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Enderdragon " + ChatColor.WHITE + "slain by " + ChatColor.YELLOW + event.getEntity().getKiller().getName(),
                            ChatColor.WHITE + sdf.format(new Date()).replace(" AM", "").replace(" PM", "")));

            dragonEgg.setItemMeta(itemMeta);

            event.getEntity().getKiller().getInventory().addItem(dragonEgg);

            if (!event.getEntity().getKiller().getInventory().contains(Material.DRAGON_EGG)) {
                event.getDrops().add(dragonEgg);
            }
        }
    }

    @EventHandler
    public void onEntityCreatePortal(EntityCreatePortalEvent event) {
        if (event.getEntity() instanceof Item && event.getPortalType() == PortalType.ENDER) {
            event.getBlocks().clear();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof EnderDragon && event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
            ((EnderDragon) event.getEntity()).setCustomName("Ender Dragon " + ChatColor.YELLOW.toString() + ChatColor.BOLD + Math.round((((EnderDragon) event.getEntity()).getHealth() / ((EnderDragon) event.getEntity()).getMaxHealth()) * 100) + "% Health");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBukkitEmpty(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatePortal(EntityCreatePortalEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if ((location.getWorld().getEnvironment() == World.Environment.THE_END) && ((location.getBlock().getType() == Material.WATER) || (location.getBlock().getType() == Material.STATIONARY_WATER))) {
            player.teleport(new Location(Bukkit.getWorld("world"), 0, Main.get().getConfig().getInt("setend.end_exit.y"), Main.get().getConfig().getInt("setend.end_exit.z")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortal(PlayerPortalEvent event) {



        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }

        Player player = event.getPlayer();

        event.setTo(new Location(Bukkit.getWorld(Main.get().getLocationsConfig().getString("END_EXIT.WORLD")), Main.get().getLocationsConfig().getInt("END_EXIT.X"), Main.get().getLocationsConfig().getInt("END_EXIT.Y"), Main.get().getLocationsConfig().getInt("END_EXIT.Z"), Main.get().getLocationsConfig().getInt("END_EXIT.YAW"), Main.get().getLocationsConfig().getInt("END_EXIT.PITCH")));


        if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {

            if (event.getFrom().getWorld().getEntitiesByClass(EnderDragon.class).size() != 0) {
                event.setCancelled(true);

                if (!(msgCooldown.containsKey(player.getName())) || msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot leave the end before the dragon is killed.");
                    msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
                }
            }

            player.teleport(new Location(Bukkit.getWorld(Main.get().getLocationsConfig().getString("END_EXIT.WORLD")), Main.get().getLocationsConfig().getInt("END_EXIT.X"), Main.get().getLocationsConfig().getInt("END_EXIT.Y"), Main.get().getLocationsConfig().getInt("END_EXIT.Z"), Main.get().getLocationsConfig().getInt("END_EXIT.YAW"), Main.get().getLocationsConfig().getInt("END_EXIT.PITCH")));
        } else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {

            if (Main.get().getTimerManager().getProtectionTimer().hasCooldown(player.getUniqueId())) {
                event.setCancelled(true);

                if (!(msgCooldown.containsKey(player.getName())) || msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter the end while you have PvP protection.");
                    msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
                }
            }

            if(Main.get().getTimerManager().getSpawnTagTimer().hasCooldown(event.getPlayer())) {
                event.setCancelled(true);

                if (!(msgCooldown.containsKey(player.getName())) || msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter the end while you are spawn tagged.");
                    msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
                }
            }

            if (!endActive && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);

                if (!(msgCooldown.containsKey(player.getName())) || msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
                    event.getPlayer().sendMessage(ChatColor.RED + "The End is currently disabled.");
                    msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
                }
            }

            for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                if (potionEffect.getDuration() < 20 * 9) {
                    event.getPlayer().removePotionEffect(potionEffect.getType());
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.blockList().clear();
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getWorld().getEnvironment() != Environment.THE_END || to.getWorld().getEnvironment() != Environment.THE_END)
            return;

        if(Main.get().getSotwTimer().getSotwRunnable() == null || SOTWCommand.enabled.contains(event.getPlayer().getUniqueId()))
            return;

        if (to.getBlockY() < -128) {
            event.getPlayer().teleport(Bukkit.getWorld(Main.get().getConfig().getString("setend.end_exit.spawn")).getSpawnLocation());
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }

}


//Algun día terminare el End, PD: Danidev819. Para: El que lo decompile :p