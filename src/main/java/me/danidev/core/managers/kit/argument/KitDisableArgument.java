package me.danidev.core.managers.kit.argument;

import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitDisableArgument extends CommandArgument {

    private final Main plugin;

    public KitDisableArgument(final Main plugin) {
        super("disable", "Disable or enable a kit");
        this.plugin = plugin;
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <kitName>";
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "No kit named " + args[1] + " found.");
            return true;
        }
        final boolean newEnabled = !kit.isEnabled();
        kit.setEnabled(newEnabled);
        sender.sendMessage(ChatColor.AQUA + "Kit " + kit.getDisplayName() + " has been " + (newEnabled ? (ChatColor.GREEN + "enabled") : (ChatColor.RED + "disabled")) + ChatColor.AQUA + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        final List<Kit> kits = this.plugin.getKitManager().getKits();
        final ArrayList<String> results = new ArrayList<String>(kits.size());
        for (final Kit kit : kits) {
            results.add(kit.getName());
        }
        return results;
    }
}
