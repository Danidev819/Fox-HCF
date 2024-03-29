package me.danidev.core.managers.timer.type;

import java.util.concurrent.TimeUnit;

import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Timer used to prevent {@link Player}s from using Notch Apples too often.
 */
public class NotchAppleTimer extends PlayerTimer implements Listener {

    private static final String GOPPLE_ART_FILE_TYPE = "png";
    private static final String GOPPLE_ART_FILE_NAME = "/gopple-art." + GOPPLE_ART_FILE_TYPE;

    // private final ImageMessage goppleArtMessage;

    public NotchAppleTimer(JavaPlugin plugin) {
        super("Notch", TimeUnit.MINUTES.toMillis(ConfigurationService.NOTCH_TIMER));
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&6&l");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1) {
            Player player = event.getPlayer();
            if (setCooldown(player, player.getUniqueId())) {

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588\u2588&c\u2588\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&c\u2588\u2588\u2588&e\u2588&c\u2588\u2588\u2588\u2588 &6&l " + this.name + ": ")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588 &7  Consumed"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&f\u2588&6\u2588&6\u2588\u2588&c\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588&f\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &6 Cooldown Remaining:"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &7  " + DurationFormatter.getRemaining(getRemaining(player), true, false))));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You still have a Notch" + ChatColor.RED + " cooldown for another " + ChatColor.BOLD + DurationFormatter.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + '.');
            }
        }
    }
}
