package me.danidev.core.listeners.fixes;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.Listener;

public class WeatherFixListener implements Listener {

    public WeatherFixListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
	
    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }
}
