package me.danidev.core.utils;

import me.danidev.core.Main;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtils {

    public static void run(Runnable runnable) {
        Main.get().getServer().getScheduler().runTask(Main.get(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Main.get().getServer().getScheduler().runTaskTimer(Main.get(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Main.get(), delay, timer);
    }

    public static void runTimerAsync(Runnable runnable, long delay, long timer) {
        Main.get().getServer().getScheduler().runTaskTimerAsynchronously(Main.get(), runnable, delay, timer);
    }

    public static void runTimerAsync(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimerAsynchronously(Main.get(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        Main.get().getServer().getScheduler().runTaskLater(Main.get(), runnable, delay);
    }

    public static void runLaterAsync(Runnable runnable, long delay) {
        try {
            Main.get().getServer().getScheduler().runTaskLaterAsynchronously(Main.get(), runnable, delay);
        }
        catch (IllegalStateException e) {
            Main.get().getServer().getScheduler().runTaskLater(Main.get(), runnable, delay);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runTaskTimerAsynchronously(Runnable runnable, int delay) {
        try {
            Main.get().getServer().getScheduler().runTaskTimerAsynchronously(Main.get(), runnable, 20L * delay, 20L * delay);
        }
        catch (IllegalStateException e) {
            Main.get().getServer().getScheduler().runTaskTimer(Main.get(), runnable, 20L * delay, 20L * delay);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void runAsync(Runnable runnable) {
        try {
            Main.get().getServer().getScheduler().runTaskAsynchronously(Main.get(), runnable);
        }
        catch (IllegalStateException e) {
            Main.get().getServer().getScheduler().runTask(Main.get(), runnable);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}