package me.danidev.core.utils;

import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CC {

    public static String BLUE;
    public static String AQUA;
    public static String YELLOW;
    public static String RED;
    public static String GRAY;
    public static String GOLD;
    public static String GREEN;
    public static String WHITE;
    public static String BLACK;
    public static String BOLD;
    public static String ITALIC;
    public static String UNDER_LINE;
    public static String STRIKE_THROUGH;
    public static String RESET;
    public static String MAGIC;
    public static String DARK_BLUE;
    public static String DARK_AQUA;
    public static String DARK_GRAY;
    public static String DARK_GREEN;
    public static String DARK_PURPLE;
    public static String DARK_RED;
    public static String PINK;
    public static String MENU_BAR;
    public static String CHAT_BAR;
    public static String SB_BAR;
    
    static {
        CC.BLUE = ChatColor.BLUE.toString();
        CC.AQUA = ChatColor.AQUA.toString();
        CC.YELLOW = ChatColor.YELLOW.toString();
        CC.RED = ChatColor.RED.toString();
        CC.GRAY = ChatColor.GRAY.toString();
        CC.GOLD = ChatColor.GOLD.toString();
        CC.GREEN = ChatColor.GREEN.toString();
        CC.WHITE = ChatColor.WHITE.toString();
        CC.BLACK = ChatColor.BLACK.toString();
        CC.BOLD = ChatColor.BOLD.toString();
        CC.ITALIC = ChatColor.ITALIC.toString();
        CC.UNDER_LINE = ChatColor.UNDERLINE.toString();
        CC.STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
        CC.RESET = ChatColor.RESET.toString();
        CC.MAGIC = ChatColor.MAGIC.toString();
        CC.DARK_BLUE = ChatColor.DARK_BLUE.toString();
        CC.DARK_AQUA = ChatColor.DARK_AQUA.toString();
        CC.DARK_GRAY = ChatColor.DARK_GRAY.toString();
        CC.DARK_GREEN = ChatColor.DARK_GREEN.toString();
        CC.DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
        CC.DARK_RED = ChatColor.DARK_RED.toString();
        CC.PINK = ChatColor.LIGHT_PURPLE.toString();
        CC.MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
        CC.CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
        CC.SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "----------------------";
    }

    public static String translate(String in) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translate(List<String> in) {
        return in.stream().map(CC::translate).collect(Collectors.toList());
    }

    public static void player(Player player, String in) {
        player.sendMessage(translate(in));
    }

    public static String strip(String in) {
        return org.bukkit.ChatColor.stripColor(in);
    }

    public static void sender(CommandSender sender, String in) {
        sender.sendMessage(translate(in));
    }

    public static void message(Player player, String in) {
        player.sendMessage(translate(in));
    }

    public static void broadcast(String in) {
        Bukkit.broadcastMessage(translate(in));
    }

    public static void log(String in) {
        Bukkit.getConsoleSender().sendMessage(translate(in));
    }

    public static String capitalize(String str) {
        return WordUtils.capitalize(str);
    }
}
