package me.danidev.core.managers.kit.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.kit.Kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitSetMinPlayTimeArgument extends CommandArgument {

    private final Main plugin;

    public KitSetMinPlayTimeArgument(Main plugin) {
        super("setminplaytime", "Sets the minimum playtime to use a kit");
        this.plugin = plugin;
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <kitName> <time>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }
        long duration = JavaUtils.parse(args[2]);
        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }
        kit.setMinPlaytimeMillis(duration);
        sender.sendMessage(ChatColor.YELLOW + "Set minimum playtime to use kit " + kit.getDisplayName() + " at " + kit.getMinPlaytimeWords() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        List<Kit> kits = this.plugin.getKitManager().getKits();
        ArrayList<String> results = new ArrayList<String>(kits.size());
        for (Kit kit : kits) {
            results.add(kit.getName());
        }
        return results;
    }
}
