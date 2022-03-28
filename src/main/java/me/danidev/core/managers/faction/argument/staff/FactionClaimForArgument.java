package me.danidev.core.managers.faction.argument.staff;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionClaimForArgument extends CommandArgument {

    private final Main plugin;

    public FactionClaimForArgument(final Main plugin) {
        super("claimfor", "Claim land for another faction.");
        this.plugin = plugin;
        this.permission = "fhcf.commands.faction.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <factionName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        final Faction targetFaction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(targetFaction instanceof ClaimableFaction)) {
            sender.sendMessage(ChatColor.RED + "Claimable faction named " + args[1] + " not found.");
            return true;
        }
        final Player player = (Player) sender;
        final WorldEditPlugin worldEditPlugin = this.plugin.getWorldEdit();
        if (worldEditPlugin == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set claim areas.");
            return true;
        }
        final Selection selection = worldEditPlugin.getSelection(player);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        final ClaimableFaction claimableFaction = (ClaimableFaction) targetFaction;
        final Claim claim = new Claim(claimableFaction, selection.getMinimumPoint(), selection.getMaximumPoint());
        if (!(claimableFaction instanceof PlayerFaction)) {
            for (final Claim prev : Lists.newArrayList(claimableFaction.getClaims())) {
                if (prev.getWorld() == claim.getWorld()) {
                    claimableFaction.removeClaim(prev, null);
                }
            }
        }
        if (claimableFaction.addClaim(claim, sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Successfully claimed this land for " + ChatColor.RED + targetFaction.getName() + ChatColor.YELLOW + '.');
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player) sender;
        final ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target)) {
                if (!results.contains(target.getName())) {
                    results.add(target.getName());
                }
            }
        }
        return results;
    }
}
