package me.danidev.core.utils.player;

import me.danidev.core.Main;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtil {

    private static final Map<Player, Location> frozen;
    private static final Map<Player, PlayerCache> playerCaches;
    private static final TObjectLongMap<Player> lastSent;

    static {
        frozen = new HashMap<>();
        playerCaches = new HashMap<>();
        lastSent = new TObjectLongHashMap<>();

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onMove(PlayerMoveEvent event) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                    Player player = event.getPlayer();
                    Location location = PlayerUtil.frozen.get(player);
                    if (location != null
                            && (to.getBlockX() != location.getBlockX() || to.getBlockZ() != location.getBlockZ()
                            || Math.abs(to.getBlockY() - location.getBlockY()) >= 2)) {
                        location.setYaw(to.getYaw());
                        location.setPitch(to.getPitch());
                        event.setTo(location);
                        long millis = System.currentTimeMillis();
                        long lastSentMillis = PlayerUtil.lastSent.get(player);
                        if (lastSentMillis != PlayerUtil.lastSent.getNoEntryValue()
                                && millis - lastSentMillis <= 3000L) {
                            return;
                        }
                        PlayerUtil.lastSent.put(player, millis);
                        player.sendMessage(ChatColor.YELLOW + "You are currently " + ChatColor.AQUA + "frozen"
                                + ChatColor.YELLOW + "!");
                    }
                }
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                Player player = event.getPlayer();
                PlayerUtil.frozen.remove(player);
                PlayerUtil.lastSent.remove(player);
                PlayerCache playerCache = PlayerUtil.playerCaches.remove(player);
                if (playerCache != null) {
                    playerCache.apply(player);
                }
            }
        }, Main.get());
    }

    public static void wipe(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setRemainingAir(player.getMaximumAir());
        player.setFireTicks(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
    }

    public static void freeze(Player player) {
        PlayerUtil.frozen.put(player, player.getLocation());
    }

    public static boolean thaw(Player player) {
        return PlayerUtil.frozen.remove(player) != null;
    }

    public static boolean isFrozen(Player player) {
        return PlayerUtil.frozen.containsKey(player);
    }

    public static void cache(Player player) {
        PlayerUtil.playerCaches.put(player, new PlayerCache(player));
    }

    public static void restore(Player player) {
        PlayerCache playerCache = PlayerUtil.playerCaches.get(player);
        if (playerCache != null) {
            playerCache.apply(player);
        }
    }

    public static PlayerCache getCache(Player player) {
        return PlayerUtil.playerCaches.get(player);
    }

    public static void denyMovement(Player player) {
        player.setWalkSpeed(0.0f);
        player.setFlySpeed(0.0f);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public static void allowMovement(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }
}
