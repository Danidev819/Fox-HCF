package me.danidev.core.utils.service;

import me.danidev.core.utils.file.FileConfig;

public final class ScoreboardService {

    public static boolean ENABLED;

    public static String TITLE;
    public static String FOOTER;

    public static String KILLS;
    public static String DEATHS;
    public static String KEYS;
    public static String STREAK;
    public static String BALANCE;

    public static String FACTION;
    public static String HOME;
    public static String DTR;
    public static String ONLINE;

    public static String SOTW;
    public static String SOTW_ENABLED;
    public static String EOTW;
    public static String EOTW_CAPPABLE;

    public static String CITADEL;
    public static String KOTH;
    public static String KOTH_COORDS;

    public static String CUSTOM_TIMER;
    public static String TIMER;

    public static String PANDA_ABILITY_GLOBAL_COOLDOWN;
    public static String PANDA_ABILITY_ABILITY_COOLDOWN;

    public static String END_EXIT;

    public static void init(FileConfig scoreboardConfig) {
        ENABLED = scoreboardConfig.getBoolean("ENABLED");

        TITLE = scoreboardConfig.getString("TITLE");
        FOOTER = scoreboardConfig.getString("FOOTER");

        KILLS = scoreboardConfig.getString("STATISTICS.KILLS");
        DEATHS = scoreboardConfig.getString("STATISTICS.DEATHS");
        KEYS = scoreboardConfig.getString("STATISTICS.KEYS");
        STREAK = scoreboardConfig.getString("STATISTICS.STREAK");
        BALANCE = scoreboardConfig.getString("STATISTICS.BALANCE");

        FACTION = scoreboardConfig.getString("FACTION_FOCUS.FACTION");
        HOME = scoreboardConfig.getString("FACTION_FOCUS.HOME");
        DTR = scoreboardConfig.getString("FACTION_FOCUS.DTR");
        ONLINE = scoreboardConfig.getString("FACTION_FOCUS.ONLINE");

        SOTW = scoreboardConfig.getString("SOTW");
        SOTW_ENABLED = scoreboardConfig.getString("SOTW_ENABLED");
        EOTW = scoreboardConfig.getString("EOTW");
        EOTW_CAPPABLE = scoreboardConfig.getString("EOTW_CAPPABLE");

        CITADEL = scoreboardConfig.getString("CITADEL");
        KOTH = scoreboardConfig.getString("KOTH");

        CUSTOM_TIMER = scoreboardConfig.getString("CUSTOM_TIMER");
        TIMER = scoreboardConfig.getString("TIMER");

        PANDA_ABILITY_GLOBAL_COOLDOWN = scoreboardConfig.getString("PANDA_ABILITY.GLOBAL_COOLDOWN");
        PANDA_ABILITY_ABILITY_COOLDOWN = scoreboardConfig.getString("PANDA_ABILITY.ABILITY_COOLDOWN");

        END_EXIT = scoreboardConfig.getString("END_EXIT");
    }
}
