package me.danidev.core.managers.timer.type.sotw;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SOTWTimer {

    private SotwRunnable sotwRunnable;

    Main plugin;

    public boolean cancel() {
        if (this.sotwRunnable != null) {
            this.sotwRunnable.cancel();
            this.sotwRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.sotwRunnable == null) {
            this.sotwRunnable = new SotwRunnable(this, millis);
            this.sotwRunnable.runTaskLater(Main.get(), millis / 50L);
        }
    }

    public static class SotwRunnable extends BukkitRunnable {

        private SOTWTimer SOTWTimer;
        private long startMillis;
        private static long endMillis;

        @SuppressWarnings("static-access")
        public SotwRunnable(SOTWTimer SOTWTimer, long duration) {
            this.SOTWTimer = SOTWTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }

        public long getRemaining() {
            return endMillis - System.currentTimeMillis();
        }

        @Override
        public void run() {
            for (String s : Main.get().getLangConfig().getStringList("SOTW-OVER")) {
                for (Player loop : Bukkit.getServer().getOnlinePlayers()) {
                    loop.sendMessage(CC.translate(s));
                }
            }
            this.cancel();
            this.SOTWTimer.sotwRunnable = null;
        }
    }


    public SotwRunnable getSotwRunnable() {
        return sotwRunnable;
    }

    public long getRemaining() {
        return SotwRunnable.endMillis - System.currentTimeMillis();
    }
}