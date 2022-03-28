package me.danidev.core.listeners.fixes;

import me.danidev.core.Main;
import me.danidev.core.managers.user.BaseUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerMonitorListener implements Listener {

    public PlayerMonitorListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BaseUser baseUser = Main.get().getUserManager().getBaseUser(player.getUniqueId());

        baseUser.tryLoggingName(player);
        baseUser.tryLoggingAddress(player.getAddress().getAddress().getHostAddress());
    }
}
