package me.danidev.core.managers.classes.mage;

import com.google.common.base.Preconditions;

public class MageData {
	
    public static double MIN_ENERGY = 0.0;
    public static double MAX_ENERGY = 120.0;

    public long buffCooldown;
    private long energyStart;
    
    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }
    
    public void startEnergyTracking() {
        this.setEnergy(0.0);
    }
    
    public long getEnergyMillis() {
        if (this.energyStart == 0L) {
            return 0L;
        }
        return Math.min(120000L, (long)(1.25 * (System.currentTimeMillis() - this.energyStart)));
    }
    
    public double getEnergy() {
        double value = this.getEnergyMillis() / 1000.0;
        return Math.round(value * 10.0) / 10.0;
    }
    
    public void setEnergy(double energy) {
        Preconditions.checkArgument(energy >= MIN_ENERGY, "Energy cannot be less than 0.0");
        Preconditions.checkArgument(energy <= MAX_ENERGY, "Energy cannot be more than 120.0");
        this.energyStart = (long)(System.currentTimeMillis() - 1000.0 * energy);
    }
}
