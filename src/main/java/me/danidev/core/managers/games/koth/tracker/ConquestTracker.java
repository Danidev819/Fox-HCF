package me.danidev.core.managers.games.koth.tracker;

import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.faction.ConquestFaction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventTimer;
import me.danidev.core.managers.games.koth.EventType;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.List;
import com.google.common.collect.Ordering;

import me.danidev.core.managers.faction.event.FactionRemoveEvent;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.apache.commons.lang3.ObjectUtils;
import com.google.common.collect.ImmutableMap;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Comparator;
import org.bukkit.event.Listener;

public class ConquestTracker implements EventTracker, Listener {

    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private static final Comparator<Map.Entry<PlayerFaction, Integer>> POINTS_COMPARATOR;
    private final Map<PlayerFaction, Integer> factionPointsMap;
    private final Main plugin;
    private int conquestPoints;
    private int conquestLossPoints;
    
    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5L);
        DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        POINTS_COMPARATOR = ((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
    }
    
    public ConquestTracker(final Main ins) {
        this.factionPointsMap = Collections.synchronizedMap(new LinkedHashMap<>());
        this.plugin = ins;
        this.setConquestPoints(ConfigurationService.CONQUEST_MAX_POINTS);
        this.setConquestLossPoints(ConfigurationService.CONQUEST_LOSS_POINTS);
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(final FactionRemoveEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            synchronized (this.factionPointsMap) {
                this.factionPointsMap.remove(faction);
            }
        }
    }
    
    public Map<PlayerFaction, Integer> getFactionPointsMap() {
        return (Map<PlayerFaction, Integer>)ImmutableMap.copyOf((Map)this.factionPointsMap);
    }
    
    public int getPoints(final PlayerFaction faction) {
        synchronized (this.factionPointsMap) {
            return (int)ObjectUtils.firstNonNull((Object[])new Integer[] { this.factionPointsMap.get(faction), 0 });
        }
    }
    
    public int setPoints(final PlayerFaction faction, final int amount) {
        if (amount < 0) {
            return amount;
        }
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.put(faction, amount);
            final List<Map.Entry<PlayerFaction, Integer>> entries = (List<Map.Entry<PlayerFaction, Integer>>)Ordering.from((Comparator)ConquestTracker.POINTS_COMPARATOR).sortedCopy(this.factionPointsMap.entrySet());
            this.factionPointsMap.clear();
            for (final Map.Entry<PlayerFaction, Integer> entry : entries) {
                this.factionPointsMap.put(entry.getKey(), entry.getValue());
            }
        }
        return amount;
    }
    
    public int takePoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, this.getPoints(faction) - amount);
    }
    
    public int addPoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, this.getPoints(faction) + amount);
    }
    
    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }
    
    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
        final List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for (final CaptureZone captureZone : captureZones) {
            final Player cappingPlayer = captureZone.getCappingPlayer();
            if (cappingPlayer == null) {
                continue;
            }
            final long remainingMillis = captureZone.getRemainingCaptureMillis();
            if (remainingMillis <= 0L) {
                final UUID uuid = cappingPlayer.getUniqueId();
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
                if (playerFaction != null) {
                    final int newPoints = this.addPoints(playerFaction, 1);
                    if (newPoints >= this.conquestPoints) {
                        synchronized (this.factionPointsMap) {
                            this.factionPointsMap.clear();
                        }
                        this.plugin.getTimerManager().eventTimer.handleWinnerConquest(cappingPlayer);
                        return;
                    }
                    captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                    Bukkit.broadcastMessage(CC.translate(ChatColor.YELLOW + "&8[&6&l" + eventFaction.getName() + "&8] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.YELLOW + " gained " + 1 + " point for capturing " + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + newPoints + '/' + this.conquestPoints + ')'));
                }
                return;
            }
            final int remainingSeconds = (int)Math.round(remainingMillis / 1000.0);
            if (remainingSeconds % 15 != 0) {
                continue;
            }
            final UUID uuid2 = cappingPlayer.getUniqueId();
            final PlayerFaction playerFaction2 = this.plugin.getFactionManager().getPlayerFaction(uuid2);
            playerFaction2.broadcast(CC.translate(ChatColor.YELLOW + "&8[&6&l" + eventFaction.getName() + "&8] " + ChatColor.GOLD + cappingPlayer.getName() + "'s &eattempting to control " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + remainingSeconds + "s)"));
            cappingPlayer.sendMessage(CC.translate(ChatColor.YELLOW + "&8[&6&l" + eventFaction.getName() + "&8] " + ChatColor.YELLOW + "Attempting to control " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + remainingSeconds + "s)"));
        }
    }
    
    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(CC.translate(ChatColor.YELLOW + "&8[&6&l" + eventFaction.getName() + "&8] " + ChatColor.GOLD + eventFaction.getName() + " &ecan now be contested."));
    }
    
    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        if (this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture for Conquest.");
            return false;
        }
        return true;
    }
    
    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > ConquestTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(CC.translate(ChatColor.YELLOW + "&8[&6&l" + eventFaction.getName() + "&8] " + ChatColor.GOLD + player.getName() + " &ewas knocked off " + captureZone.getDisplayName() + ChatColor.YELLOW + '.'));
        }
        return true;
    }
    
    @Override
    public void stopTiming() {
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.clear();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Faction currentEventFac = this.plugin.getTimerManager().eventTimer.getEventFaction();
        if (currentEventFac instanceof ConquestFaction) {
            final Player player = event.getEntity();
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
            if (playerFaction != null) {
                final int oldPoints = this.getPoints(playerFaction);
                if (oldPoints == 0) {
                    return;
                }
                if (this.getPoints(playerFaction) <= this.conquestLossPoints) {
                    this.setPoints(playerFaction, 0);
                }
                else {
                    this.takePoints(playerFaction, this.conquestLossPoints);
                }
                event.setDeathMessage(CC.translate(ChatColor.YELLOW + "&8[&6&l" + currentEventFac.getName() + "&8] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.YELLOW + " lost " + ChatColor.RED + Math.min(this.conquestLossPoints, oldPoints) + ChatColor.YELLOW + " points because " + player.getName() + " died." + ChatColor.RED + " (" + this.getPoints(playerFaction) + '/' + this.conquestPoints + ')' + ChatColor.YELLOW + '.'));
            }
        }
    }
    
    public int getConquestPoints() {
        return this.conquestPoints;
    }
    
    public void setConquestPoints(final int conquestPoints) {
        this.conquestPoints = conquestPoints;
    }
    
    public int getConquestLossPoints() {
        return this.conquestLossPoints;
    }
    
    public int setConquestLossPoints(final int conquestLossPoints) {
        return this.conquestLossPoints = conquestLossPoints;
    }
}
