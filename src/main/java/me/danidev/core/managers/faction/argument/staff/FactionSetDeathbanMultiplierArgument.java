package me.danidev.core.managers.faction.argument.staff;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.type.Faction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionSetDeathbanMultiplierArgument extends CommandArgument {

    private static final double MIN_MULTIPLIER = 0.0;
    private static final double MAX_MULTIPLIER = 5.0;
    private final Main plugin;

    public FactionSetDeathbanMultiplierArgument(final Main plugin) {
        super("setdeathbanmultiplier", "Put a deathban multiplication.");
        this.plugin = plugin;
        this.permission = "fhcf.command.faction.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName|factionName> <newMultiplier>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        final Double multiplier = JavaUtils.tryParseDouble(args[2]);
        if (multiplier == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
            return true;
        }
        if (multiplier < 0.0) {
            sender.sendMessage(ChatColor.RED + "Deathban multipliers may not be less than " + MIN_MULTIPLIER + '.');
            return true;
        }
        if (multiplier > 5.0) {
            sender.sendMessage(ChatColor.RED + "Deathban multipliers may not be more than " + MAX_MULTIPLIER + '.');
            return true;
        }
        final double previousMultiplier = faction.getDeathbanMultiplier();
        faction.setDeathbanMultiplier(multiplier);
        sender.sendMessage(CC.translate("&eSet deathban multiplier of " + faction.getName() + " from " + previousMultiplier + " to " + multiplier + "."));
        return true;
    }
}
