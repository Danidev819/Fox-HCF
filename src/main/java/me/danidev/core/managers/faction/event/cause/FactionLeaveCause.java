package me.danidev.core.managers.faction.event.cause;

public enum FactionLeaveCause
{
    KICK("KICK", 0), 
    LEAVE("LEAVE", 1), 
    DISBAND("DISBAND", 2);
    
    private FactionLeaveCause(final String s, final int n) {
    }
}
