package me.danidev.core.managers.classes.mage;

import org.bukkit.potion.PotionEffect;

public class MageEffect {

    public int energyCost;
    public PotionEffect clickable;

    public MageEffect(int energyCost, PotionEffect clickable) {
        this.energyCost = energyCost;
        this.clickable = clickable;
    }
}
