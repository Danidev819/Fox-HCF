package me.danidev.core.managers.games.koth;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelTracker;
import me.danidev.core.managers.games.koth.tracker.ConquestTracker;
import me.danidev.core.managers.games.koth.tracker.EventTracker;
import me.danidev.core.managers.games.koth.tracker.KothTracker;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

public enum EventType {
    
    CONQUEST("CONQUEST", 0, "Conquest", new ConquestTracker(Main.get())),
    CITADEL("CITADEL", 1, "Citadel", new CitadelTracker(Main.get())),
    KOTH("KOTH", 2, "Koth", new KothTracker(Main.get()));
    
    private static final ImmutableMap<String, EventType> byDisplayName;
    private final EventTracker eventTracker;
    private final String displayName;
    
    static {
        ImmutableBiMap.Builder<String, EventType> builder = new ImmutableBiMap.Builder<>();
        EventType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EventType eventType = values[i];
            builder.put(eventType.displayName.toLowerCase(), eventType);
        }
        byDisplayName = builder.build();
    }
    
    EventType(String s, int n, String displayName, EventTracker eventTracker) {
        this.displayName = displayName;
        this.eventTracker = eventTracker;
    }
    
    @Deprecated
    public static EventType getByDisplayName(String name) {
        return EventType.byDisplayName.get(name.toLowerCase());
    }
    
    public EventTracker getEventTracker() {
        return this.eventTracker;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
}
