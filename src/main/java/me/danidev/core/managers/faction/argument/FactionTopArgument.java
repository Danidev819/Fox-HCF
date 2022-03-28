package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FactionTopArgument extends CommandArgument {

    private Main plugin;
    public static final Comparator<PlayerFaction> POINT_COMPARATOR = Comparator.comparingInt(PlayerFaction::getPoints);

    public FactionTopArgument(Main plugin) {
        super("top", "Show top factions.", new String[]{"topfac", "topfaction"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <balance, points, koth, conquest>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Top 10 Points Factions");
        List<PlayerFaction> PlayerFactions = new ArrayList<>(plugin.getFactionManager().getFactions().stream().filter(x -> x instanceof PlayerFaction).map(x -> (PlayerFaction) x).filter(x -> x.getPoints() > 0).collect(Collectors.toSet()));
        if (PlayerFactions.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No one is good enough for points at the moment.");
        }
        PlayerFactions.sort(POINT_COMPARATOR);
        Collections.reverse(PlayerFactions);
        for (int i = 0; i < 10; i++) {
            if (i >= PlayerFactions.size()) {
                break;
            }
            PlayerFaction next = PlayerFactions.get(i);
            sender.sendMessage("" + ChatColor.GRAY + (i + 1) + ". " + ChatColor.YELLOW + next.getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + next.getPoints());
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
}
