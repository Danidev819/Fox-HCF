package me.danidev.core.managers;

import me.danidev.core.utils.chat.Text;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.HashMap;

public class MessageManager {

    private final HashMap<UUID, Long> messageDelay;

    public MessageManager() {
        this.messageDelay = new HashMap<>();
    }
    
    public void sendMessage(Player player, String message) {
        if (this.messageDelay.containsKey(player.getUniqueId())) {
            if (this.messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0L) return;

            this.messageDelay.remove(player.getUniqueId());
        }
        this.messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000L);
        player.sendMessage(message);
    }
    
    public void sendMessage(Player player, Text text) {
        if (this.messageDelay.containsKey(player.getUniqueId())) {
            if (this.messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0L) return;

            this.messageDelay.remove(player.getUniqueId());
        }
        this.messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000L);
        text.send(player);
    }
    
    public static void sendMessage(String message, String permission) {
        Bukkit.getServer().getOnlinePlayers().forEach(online -> {
            if (online.hasPermission(permission)) {
                online.sendMessage(message);
            }
        });
    }
}
