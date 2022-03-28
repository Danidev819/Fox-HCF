package me.danidev.core.managers.timer.type.ktk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class KingCommand implements CommandExecutor, TabCompleter {

    public static ArrayList<UUID> play = new ArrayList<UUID>();
    public static Player player;
    public static String kingName = "";
    public static String kingPrize = "";

    public KingCommand(Main plugin) {
        plugin.getCommand("ktk").setExecutor(this);
    }

    @SuppressWarnings("unused")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fhcf.command.kingevent")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length == 0 || args.length > 3) {
            sender.sendMessage(CC.translate(
                    "&7&m--------------------------------"));
            sender.sendMessage(CC.translate(
                    "&6&lKing Event"));
            sender.sendMessage(CC.translate(
                    ""));
            sender.sendMessage(CC.translate(
                    "&e/kingevent start <player>&7 - &fStarts the King Event."));
            sender.sendMessage(CC.translate(
                    "&e/kingevent end &7- &fEnds the current king event."));
            sender.sendMessage(CC.translate(
                    "&e/kingevent prize <prize> &7- &fSets the prize for the current king event."));
            sender.sendMessage(CC.translate(
                    "&7&m--------------------------------"));
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("end")) {
                player = null;
                return true;

            } else {
                sender.sendMessage(ChatColor.RED + "Unknown sub-command!");
                return true;
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("prize")) {
            kingPrize = args[1].replaceAll("_", " ");
            sender.sendMessage(ChatColor.GREEN + "You have successfully set the prize to " + kingPrize);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("prize")) {
            kingPrize = args[1].replaceAll("_", " ");
            sender.sendMessage(ChatColor.GREEN + "You have successfully set the prize to " + kingPrize);
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {

                Player p = Bukkit.getPlayer(args[1]);
                kingName = p.getName();
                Player player1 = Bukkit.getPlayer(KingCommand.kingName);


                sender.sendMessage(ChatColor.GREEN + "You have successfully started the king event!");
                Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), Main.get().getMainConfig().getString("KING-EVENT.START-KIT").replace("%player%", kingName));

                Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "");
                Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "King Event");
                Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "");
                Bukkit.broadcastMessage(CC.translate(" &7» &eKing" + ChatColor.GRAY + ": " + ChatColor.WHITE + kingName));
                Bukkit.broadcastMessage(CC.translate(" &7» &eLocation" + ChatColor.GRAY + ": " + ChatColor.WHITE + "x" + player1.getLocation().getBlockX() + ", y" + player1.getLocation().getBlockY() + ", z" + player1.getLocation().getBlockZ()));
                Bukkit.broadcastMessage(CC.translate(" &7» &eStarting Health" + ChatColor.GRAY + ": " + ChatColor.WHITE + player1.getHealthScale()));
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + BukkitUtils.STRAIGHT_LINE_DEFAULT);
                if(p == null) {
                    sender.sendMessage(ChatColor.RED + "That player is not online.");
                    return true;
                }
                player = p;

            } else {
                sender.sendMessage(ChatColor.RED + "Unknown sub-command!");
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
