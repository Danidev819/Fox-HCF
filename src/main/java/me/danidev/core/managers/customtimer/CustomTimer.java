package me.danidev.core.managers.customtimer;

import me.danidev.core.Main;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomTimer {

    private String name;
    private String scoreboard;
    private long startMillis;
    private long endMillis;
    private long getRemaining;
    private BukkitTask task;

    public CustomTimer(String name, String scoreboard, long startMillis, long endMillis) {
        setName(name);
        setScoreboard(scoreboard);
        setStartMillis(startMillis);
        setEndMillis(endMillis);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (endMillis < System.currentTimeMillis()) {
                    Main.get().getCustomTimerManager().deleteTimer(Main.get().getCustomTimerManager().getCustomTimer(name));
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Main.get(), 0L, 20L);
    }

    public long getRemaining(){
       return endMillis - System.currentTimeMillis();
    }

    public void cancel() {
        Main.get().getCustomTimerManager().deleteTimer(this);
    }
}
