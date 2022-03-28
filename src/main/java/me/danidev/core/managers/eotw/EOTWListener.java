package me.danidev.core.managers.eotw;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.event.FactionClaimChangeEvent;
import me.danidev.core.managers.faction.event.FactionCreateEvent;
import me.danidev.core.managers.faction.event.cause.ClaimChangeCause;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener used to handle events for if EOTW is active.
 */
public class EOTWListener implements Listener {

    private Main plugin;

    public EOTWListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        EOTWHandler.EotwRunnable runnable = plugin.getEotwHandler().getRunnable();
        if (runnable != null)
            runnable.handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        EOTWHandler.EotwRunnable runnable = plugin.getEotwHandler().getRunnable();
        if (runnable != null)
            runnable.handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        EOTWHandler.EotwRunnable runnable = plugin.getEotwHandler().getRunnable();
        if (runnable != null)
            runnable.handleDisconnect(event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH) public void onFactionCreate(FactionCreateEvent event) { if (plugin.getEotwHandler().isEndOfTheWorld()) { Faction faction =
            event.getFaction(); if (faction instanceof PlayerFaction) { event.setCancelled(true); event.getSender().sendMessage(ChatColor.RED + "Player based factions cannot be created during EOTW."); } }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionClaimChange(FactionClaimChangeEvent event) {
        if (plugin.getEotwHandler().isEndOfTheWorld() && event.getCause() == ClaimChangeCause.CLAIM) {
            Faction faction = event.getClaimableFaction();
            if (faction instanceof PlayerFaction) {
                event.setCancelled(true);
                event.getSender().sendMessage(ChatColor.RED + "Player based faction land cannot be claimed during EOTW.");
            }
        }
    }
}