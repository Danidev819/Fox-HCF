package me.danidev.core.managers.faction.struct;

public interface Raidable
{
    boolean isRaidable();
    
    double getDeathsUntilRaidable();
    
    double getMaximumDeathsUntilRaidable();
    
    double setDeathsUntilRaidable(final double p0);
    
    long getRemainingRegenerationTime();
    
    void setRemainingRegenerationTime(final long p0);
    
    RegenStatus getRegenStatus();
}
