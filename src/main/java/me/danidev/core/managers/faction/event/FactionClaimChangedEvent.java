package me.danidev.core.managers.faction.event;

import java.util.Collection;

import me.danidev.core.managers.faction.event.cause.ClaimChangeCause;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import me.danidev.core.managers.faction.claim.Claim;

import org.bukkit.event.Event;

public class FactionClaimChangedEvent extends Event
{
    private static final HandlerList handlers;
    private final CommandSender sender;
    private final ClaimChangeCause cause;
    private final Collection<Claim> affectedClaims;
    
    static {
        handlers = new HandlerList();
    }
    
    public FactionClaimChangedEvent(final CommandSender sender, final ClaimChangeCause cause, final Collection<Claim> affectedClaims) {
        this.sender = sender;
        this.cause = cause;
        this.affectedClaims = affectedClaims;
    }
    
    public static HandlerList getHandlerList() {
        return FactionClaimChangedEvent.handlers;
    }
    
    public CommandSender getSender() {
        return this.sender;
    }
    
    public ClaimChangeCause getCause() {
        return this.cause;
    }
    
    public Collection<Claim> getAffectedClaims() {
        return this.affectedClaims;
    }
    
    public HandlerList getHandlers() {
        return FactionClaimChangedEvent.handlers;
    }
}
