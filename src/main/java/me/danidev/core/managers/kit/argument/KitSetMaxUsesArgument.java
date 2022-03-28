package me.danidev.core.managers.kit.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.others.Ints;
import com.google.common.collect.ImmutableList;

import me.danidev.core.managers.kit.Kit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KitSetMaxUsesArgument extends CommandArgument {

    private static final List<String> COMPLETIONS_THIRD;

    static {
        COMPLETIONS_THIRD = ImmutableList.of("UNLIMITED");
    }

    private final Main plugin;

    public KitSetMaxUsesArgument(final Main plugin) {
        super("setmaxuses", "Sets the maximum uses for a kit");
        this.plugin = plugin;
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kitName> <amount|unlimited>";
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }
        Integer amount;
        if (KitSetMaxUsesArgument.COMPLETIONS_THIRD.contains(args[2])) {
            amount = Integer.MAX_VALUE;
        } else {
            amount = Ints.tryParse(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
                return true;
            }
        }
        kit.setMaximumUses(amount);
        sender.sendMessage(ChatColor.GRAY + "Set maximum uses of kit " + kit.getDisplayName() + " to " + ((amount == Integer.MAX_VALUE) ? "unlimited" : amount) + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Collection<Kit> kits = this.plugin.getKitManager().getKits();
            final List<String> results = new ArrayList<String>(kits.size());
            for (final Kit kit : kits) {
                results.add(kit.getName());
            }
            return results;
        }
        if (args.length == 3) {
            return KitSetMaxUsesArgument.COMPLETIONS_THIRD;
        }
        return Collections.emptyList();
    }
}
