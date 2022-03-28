package me.danidev.core.managers.faction.struct;

public enum RegenStatus {

    FULL("FULL", 0,  "&a◀"),
    REGENERATING("REGENERATING", 1, "&a▲"),
    PAUSED("PAUSED", 2, "&4&l■");
    
    private final String symbol;
    
    RegenStatus(final String s, final int n, final String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
}
