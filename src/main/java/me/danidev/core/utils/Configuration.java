package me.danidev.core.utils;

import java.util.EnumMap;
import java.util.Map;
import me.danidev.core.Main;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class Configuration {

    public static void init(final FileConfiguration config) {

        ConfigurationSection bor = Main.get().getMainConfig().getConfiguration().getConfigurationSection("BORDER");
        if (bor != null) {
            BORDER_SIZES.clear();
            for (String key : bor.getKeys(false)) {
                World.Environment env = World.Environment.valueOf(key);
                if (env != null)
                    BORDER_SIZES.put(env, bor.getInt(key));
                else
                    System.out.println("Could not find world type with name " + key);
            }
        }
    }

    public static Map<World.Environment, Integer> BORDER_SIZES = new EnumMap<>(World.Environment.class);

    static {
        BORDER_SIZES.put(World.Environment.NORMAL, Main.get().getMainConfig().getInt("BORDER.OVERWORLD"));
        BORDER_SIZES.put(World.Environment.NETHER, Main.get().getMainConfig().getInt("BORDER.NETHER"));
        BORDER_SIZES.put(World.Environment.THE_END, Main.get().getMainConfig().getInt("BORDER.END"));
    }
}