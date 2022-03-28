package me.danidev.core.managers.timer.type.ktk;

import java.util.concurrent.TimeUnit;

import me.danidev.core.Main;
import me.danidev.core.managers.timer.GlobalTimer;
import me.danidev.core.managers.timer.event.TimerExpireEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KingTimer extends GlobalTimer implements Listener {

    private final Main plugin;

    public KingTimer(Main plugin) {
        super("King",  TimeUnit.SECONDS.toMillis(1L));
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpire(TimerExpireEvent event) {
        if (event.getTimer() == this) {
        }
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD.toString();
    }
}
