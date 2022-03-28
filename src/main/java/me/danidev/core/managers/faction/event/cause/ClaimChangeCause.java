package me.danidev.core.managers.faction.event.cause;

public enum ClaimChangeCause
{
    UNCLAIM("UNCLAIM", 0), 
    CLAIM("CLAIM", 1), 
    RESIZE("RESIZE", 2);
    
    private ClaimChangeCause(final String s, final int n) {
    }
}
