package me.danidev.core.managers.faction.argument;

import java.util.Collection;

import me.danidev.core.Main;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.others.MapSorting;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;

import me.danidev.core.managers.faction.type.PlayerFaction;
import net.md_5.bungee.api.chat.BaseComponent;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionListArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionListArgument(final Main plugin) {
        super("list", "All factions.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return String.valueOf('/') + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Integer page;
        if (args.length < 2) {
            page = 1;
        }
        else {
            page = JavaUtils.tryParseInt(args[1]);
            if (page == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                return true;
            }
        }
        new BukkitRunnable() {
            public void run() {
                FactionListArgument.this.showList(page, label, sender);
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
        return true;
    }
    
    @SuppressWarnings("deprecation")
	private void showList(final int pageNumber, final String label, final CommandSender sender) {
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        final Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<PlayerFaction, Integer>();
        final Player senderPlayer = (Player)sender;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (senderPlayer == null || senderPlayer.canSee(target)) {
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(target);
                if (playerFaction != null) {
                    factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
                }
            }
        }
        final Map<Integer, List<BaseComponent[]>> pages = new HashMap<Integer, List<BaseComponent[]>>();
        final List<Map.Entry<PlayerFaction, Integer>> sortedMap = (List<Map.Entry<PlayerFaction, Integer>>) MapSorting.sortedValues(factionOnlineMap, Comparator.reverseOrder());
        for (final Map.Entry<PlayerFaction, Integer> entry : sortedMap) {
            int currentPage = pages.size();
            List<BaseComponent[]> results = pages.get(currentPage);
            if (results == null || results.size() >= 10) {
                pages.put(++currentPage, results = new ArrayList<BaseComponent[]>(10));
            }
            final PlayerFaction playerFaction2 = entry.getKey();
            final String displayName = playerFaction2.getName();
            final int index = results.size() + ((currentPage > 1) ? ((currentPage - 1) * 10) : 0) + 1;
            final ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(ChatColor.GREEN + ChatColor.stripColor(displayName)).color(net.md_5.bungee.api.ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.valueOf('/') + label + " show " + playerFaction2.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.WHITE + "Click to view " + displayName + ChatColor.WHITE + '.').create()));
            builder.append(" (" + ChatColor.WHITE + entry.getValue() + '/' + playerFaction2.getMembers().size() + ")" + ChatColor.GRAY + " [" + playerFaction2.getDtrColour() + JavaUtils.format((Number)playerFaction2.getDeathsUntilRaidable(false)) + ChatColor.GRAY + "/" + playerFaction2.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "] ", ComponentBuilder.FormatRetention.FORMATTING).color(net.md_5.bungee.api.ChatColor.WHITE);
            results.add(builder.create());
        }
        final int maxPages = pages.size();
        if (pageNumber > maxPages) {
            sender.sendMessage(ChatColor.RED + "There " + ((maxPages == 1) ? ("is only " + maxPages + " page") : "no factions to be displayed at this time") + ".");
            return;
        }
        sender.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        sender.sendMessage(String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + " Faction List " + ChatColor.GRAY + "(Page " + pageNumber + " out of " + maxPages + " pages)");
        final Player player = (Player)sender;
        final Collection<BaseComponent[]> components = pages.get(pageNumber);
        for (final BaseComponent[] component : components) {
            if (component == null) {
                continue;
            }
            if (player != null) {
                player.spigot().sendMessage(component);
            }
            else {
                sender.sendMessage(TextComponent.toPlainText(component));
            }
        }
        sender.sendMessage(ChatColor.AQUA + " Use " + ChatColor.GRAY + '/' + label + ' ' + this.getName() + " <#>" + ChatColor.AQUA + " to view the other pages.");
        sender.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }
}
