package me.danidev.core.utils;

import me.danidev.core.Main;

public class ConfigEnd
{
    public static Main plugin;

    public ConfigEnd(Main instance) {
        ConfigEnd.plugin = instance;
    }

    public static String getString(final String string) {
        return Main.get().getMainConfig().getString("setend." + string);
    }

    public static Integer getInt(final String string) {
        return Main.get().getMainConfig().getInt("setend." + string);
    }

    public static Double getDouble(final String string) {
        return Main.get().getMainConfig().getDouble("setend." + string);
    }

    public static void reload() {
        ConfigEnd.plugin.reloadConfig();
    }

    public static void set(final Object path, final Object result) {
        Main.get().getMainConfig().getConfiguration().set("setend." + path, result);
        Main.get().getMainConfig().save();
    }
}
