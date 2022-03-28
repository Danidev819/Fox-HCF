package me.danidev.core.managers.timer.type.ktk;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class KingListener implements Listener {

    private RebootRunnable rebootRunnable;

    Main plugin;

    public boolean cancel() {
        if (this.rebootRunnable != null) {
            this.rebootRunnable.cancel();
            this.rebootRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.rebootRunnable == null) {
            this.rebootRunnable = new RebootRunnable(this, millis);
            this.rebootRunnable.runTaskLater(Main.get(), millis / 50L);
        }
    }

    public static class RebootRunnable extends BukkitRunnable {

        private KingListener rebootTimer;
        private long startMillis;
        private static long endMillis;

        @SuppressWarnings("static-access")
        public RebootRunnable(KingListener rebootTimer, long duration) {
            this.rebootTimer = rebootTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }

        public long getRemaining() {
            return endMillis - System.currentTimeMillis();
        }

        @Override
        public void run() {
            this.cancel();
            this.rebootTimer.rebootRunnable = null;
        }
    }

    public RebootRunnable getRebootRunnable() {
        return rebootRunnable;
    }

    public long getRemaining() {
        return RebootRunnable.endMillis - System.currentTimeMillis();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if(KingCommand.player != null) {
            if(player.getName().equals(KingCommand.kingName)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kingevent end");
                Bukkit.broadcastMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "THE KING HAS DIED");
            }
        }
    }
}
