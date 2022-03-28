package me.danidev.core.managers.faction.event;

import me.danidev.core.managers.faction.struct.Relation;
import org.bukkit.event.HandlerList;

import me.danidev.core.managers.faction.type.PlayerFaction;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class FactionRelationCreateEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private final PlayerFaction senderFaction;
    private final PlayerFaction targetFaction;
    private final Relation relation;
    private boolean cancelled;
    
    static {
        handlers = new HandlerList();
    }
    
    public FactionRelationCreateEvent(final PlayerFaction senderFaction, final PlayerFaction targetFaction, final Relation relation) {
        this.senderFaction = senderFaction;
        this.targetFaction = targetFaction;
        this.relation = relation;
    }
    
    public static HandlerList getHandlerList() {
        return FactionRelationCreateEvent.handlers;
    }
    
    public PlayerFaction getSenderFaction() {
        return this.senderFaction;
    }
    
    public PlayerFaction getTargetFaction() {
        return this.targetFaction;
    }
    
    public Relation getRelation() {
        return this.relation;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
    
    public HandlerList getHandlers() {
        return FactionRelationCreateEvent.handlers;
    }
}
