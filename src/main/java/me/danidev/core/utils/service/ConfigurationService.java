package me.danidev.core.utils.service;

import me.danidev.core.utils.file.FileConfig;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ConfigurationService {

    public static boolean KITMAP;
    public static String DOUBLE_ARROW;

    public static String TEAMSPEAK;
    public static String STORE;
    public static String TWITTER;
    public static String DISCORD;
    public static String WEBSITE;

    public static int BORDER_OVERWORLD;
    public static int BORDER_NETHER;
    public static int BORDER_END;

    public static Map<World.Environment, Integer> BORDER_SIZES;

    public static int WARZONE_RADIUS;
    public static int BUILD_RADIUS;

    public static Map<World.Environment, Double> SPAWN_RADIUS;

    public static long DEATHBAN_DURATION;

    public static int MAX_PLAYERS_PER_FACTION;
    public static int MAX_ALLIES_PER_FACTION;
    public static long DTR_MILLIS_BETWEEN_UPDATES;
    public static String DTR_WORDS_BETWEEN_UPDATES;
    public static double DTR_LOSS_WORLD;
    public static double DTR_LOSS_NETHER;
    public static double DTR_LOSS_END;
    public static double DTR_LOSS_DEATH_ROOM;

    public static String DEATH_TIME_KITMAP;
    public static String DEATH_TIME_HCF;

    public static ChatColor TEAMMATE_COLOR;
    public static ChatColor ALLY_COLOR;
    public static ChatColor ENEMY_COLOR;
    public static ChatColor ARCHER_TAG_COLOR;
    public static ChatColor TARGET_COLOR;
    public static ChatColor SAFEZONE_COLOR;
    public static ChatColor WILDERNESS_COLOR;
    public static ChatColor WARZONE_COLOR;
    public static ChatColor ROAD_COLOR;
    public static ChatColor GLOWSTONE_COLOR;

    public static int CONQUEST_MAX_POINTS;
    public static int CONQUEST_WIN_POINTS;
    public static int CONQUEST_LOSS_POINTS;
    
    public static int ENDERPEARL_TIMER;
    public static int ARCHER_TAG_TIMER;
    public static int LOGOUT_TIMER;
    public static int NOTCH_TIMER;
    public static int GOLDEN_APPLE_TIMER;
    public static int CLASS_WARMUP_TIMER;
    public static int PROTECTION_TIMER;
    public static int SPAWN_TAG_TIMER;
    public static int STUCK_TIMER;
    public static int TELEPORT_TIMER;

    public static int POINTS_PER_KILL;
    public static int POINTS_PER_DEATH;

    public static List<Short> POTIONS_DISABLED;

    public static void init(FileConfig mainConfig) {
        KITMAP = mainConfig.getBoolean("KITMAP");
        DOUBLE_ARROW = "\u00BB";

        TEAMSPEAK = mainConfig.getString("TEAMSPEAK");
        STORE = mainConfig.getString("STORE");
        DISCORD = mainConfig.getString("DISCORD");
        TWITTER = mainConfig.getString("TWITTER");
        WEBSITE = mainConfig.getString("WEBSITE");

        BORDER_OVERWORLD = mainConfig.getInt("BORDER.OVERWORLD");
        BORDER_NETHER = mainConfig.getInt("BORDER.NETHER");
        BORDER_END = mainConfig.getInt("BORDER.END");

        BORDER_SIZES.put(World.Environment.NORMAL, BORDER_OVERWORLD);
        BORDER_SIZES.put(World.Environment.NETHER, BORDER_NETHER);
        BORDER_SIZES.put(World.Environment.THE_END, BORDER_END);

        WARZONE_RADIUS = mainConfig.getInt("RADIUS.WARZONE");
        BUILD_RADIUS = mainConfig.getInt("RADIUS.BUILD");

        SPAWN_RADIUS.put(World.Environment.NORMAL, 63.0);
        SPAWN_RADIUS.put(World.Environment.NETHER, 22.5);
        SPAWN_RADIUS.put(World.Environment.THE_END, 48.5);

        DEATHBAN_DURATION = TimeUnit.MINUTES.toMillis(mainConfig.getInt("DEATHBAN"));

        MAX_PLAYERS_PER_FACTION = mainConfig.getInt("FACTION_GENERAL.MAX_PLAYERS");
        MAX_ALLIES_PER_FACTION = mainConfig.getInt("FACTION_GENERAL.MAX_ALLIES");

        DTR_MILLIS_BETWEEN_UPDATES = TimeUnit.SECONDS.toMillis(45L);
        //DTR_WORDS_BETWEEN_UPDATES = DurationFormatUtils.formatDurationWords(DTR_MILLIS_BETWEEN_UPDATES, true, true);

        DTR_LOSS_WORLD = mainConfig.getDouble("FACTION_GENERAL.DTR_LOSS.WORLD");
        DTR_LOSS_NETHER = mainConfig.getDouble("FACTION_GENERAL.DTR_LOSS.NETHER");
        DTR_LOSS_END = mainConfig.getDouble("FACTION_GENERAL.DTR_LOSS.END");
        DTR_LOSS_DEATH_ROOM = mainConfig.getDouble("FACTION_GENERAL.DTR_LOSS.DEATH_ROOM");

        DEATH_TIME_KITMAP = mainConfig.getString("FACTION_GENERAL.DEATH_TIME.KITMAP");
        DEATH_TIME_HCF = mainConfig.getString("FACTION_GENERAL.DEATH_TIME.HCF");

        TEAMMATE_COLOR = ChatColor.DARK_GREEN;
        ALLY_COLOR = ChatColor.BLUE;
        ENEMY_COLOR = ChatColor.RED;
        ARCHER_TAG_COLOR = ChatColor.YELLOW;
        TARGET_COLOR = ChatColor.RED;
        SAFEZONE_COLOR = ChatColor.GREEN;
        WILDERNESS_COLOR = ChatColor.DARK_GREEN;
        WARZONE_COLOR = ChatColor.DARK_RED;
        ROAD_COLOR = ChatColor.GOLD;
        GLOWSTONE_COLOR = ChatColor.GOLD;

        CONQUEST_MAX_POINTS = mainConfig.getInt("CONQUEST.MAX_POINTS");
        CONQUEST_WIN_POINTS = mainConfig.getInt("CONQUEST.WIN_POINTS");
        CONQUEST_LOSS_POINTS = mainConfig.getInt("CONQUEST.LOSS_POINTS");

        LOGOUT_TIMER = mainConfig.getInt("TIMERS.LOGOUT");
        ENDERPEARL_TIMER = mainConfig.getInt("TIMERS.ENDERPEARL");
        ARCHER_TAG_TIMER = mainConfig.getInt("TIMERS.ARCHER_TAG");
        NOTCH_TIMER = mainConfig.getInt("TIMERS.NOTCH");
        GOLDEN_APPLE_TIMER = mainConfig.getInt("TIMERS.GOLDEN_APPLE");
        CLASS_WARMUP_TIMER = mainConfig.getInt("TIMERS.CLASS_WARMUP");
        PROTECTION_TIMER = mainConfig.getInt("TIMERS.PROTECTION");
        SPAWN_TAG_TIMER = mainConfig.getInt("TIMERS.SPAWN_TAG");
        STUCK_TIMER = mainConfig.getInt("TIMERS.STUCK");
        TELEPORT_TIMER = mainConfig.getInt("TIMERS.TELEPORT");

        POINTS_PER_KILL = mainConfig.getInt("POINTS.PER_KILL");
        POINTS_PER_DEATH = mainConfig.getInt("POINTS.PER_DEATH");

        POTIONS_DISABLED = mainConfig.getConfiguration().getShortList("POTIONS_DISABLED");
    }

    static {
        BORDER_SIZES = new EnumMap<>(World.Environment.class);
        SPAWN_RADIUS = new EnumMap<>(World.Environment.class);
    }
}
