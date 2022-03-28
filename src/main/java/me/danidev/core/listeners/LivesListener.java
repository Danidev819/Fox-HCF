package me.danidev.core.listeners;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LivesListener implements Listener {

    public LivesListener() {
        Bukkit.getPluginManager().registerEvents(this, Main.get());
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Main.get().getLivesManager().load(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Main.get().getLivesManager().save(event.getPlayer().getUniqueId());
    }
}