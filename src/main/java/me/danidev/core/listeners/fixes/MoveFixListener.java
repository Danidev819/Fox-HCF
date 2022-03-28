package me.danidev.core.listeners.fixes;

import me.danidev.core.listeners.event.PlayerMoveByBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveFixListener implements Listener {
	
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new PlayerMoveByBlockEvent(event.getPlayer(), event.getTo(), event.getFrom()));
    }
}
