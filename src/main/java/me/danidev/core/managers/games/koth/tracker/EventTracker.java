package me.danidev.core.managers.games.koth.tracker;

import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventTimer;
import me.danidev.core.managers.games.koth.EventType;
import org.bukkit.entity.Player;

@Deprecated //TODO: Arreglar
public interface EventTracker {

    EventType getEventType();
    
    void tick(final EventTimer p0, final EventFaction p1);
    
    void onContest(final EventFaction p0, final EventTimer p1);
    
    boolean onControlTake(final Player p0, final CaptureZone p1);
    
    boolean onControlLoss(final Player p0, final CaptureZone p1, final EventFaction p2);
    
    void stopTiming();
}
