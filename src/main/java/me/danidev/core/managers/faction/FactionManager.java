package me.danidev.core.managers.faction;

import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.World;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.time.DurationFormatUtils;
import java.util.concurrent.TimeUnit;

public interface FactionManager {

    long MAX_DTR_REGEN_MILLIS = TimeUnit.MINUTES.toMillis(30L);
    String MAX_DTR_REGEN_WORDS = DurationFormatUtils.formatDurationWords(FactionManager.MAX_DTR_REGEN_MILLIS, true, true);
    
    Map<String, ?> getFactionNameMap();
    
    Collection<Faction> getFactions();
    
    Claim getClaimAt(final Location p0);
    
    Claim getClaimAt(final World p0, final int p1, final int p2);
    
    Faction getFactionAt(final Location p0);
    
    Faction getFactionAt(final Block p0);
    
    Faction getFactionAt(final World p0, final int p1, final int p2);
    
    Faction getFaction(final String p0);
    
    Faction getFaction(final UUID p0);
    
    @Deprecated
    PlayerFaction getContainingPlayerFaction(final String p0);
    
    @Deprecated
    PlayerFaction getPlayerFaction(final Player p0);
    
    PlayerFaction getPlayerFaction(final UUID p0);
    
    Faction getContainingFaction(final String p0);
    
    boolean containsFaction(final Faction p0);
    
    boolean createFaction(final Faction p0);
    
    boolean createFaction(final Faction p0, final CommandSender p1);
    
    boolean removeFaction(final Faction p0, final CommandSender p1);
    
    void reloadFactionData();
    
    void saveFactionData();
}
