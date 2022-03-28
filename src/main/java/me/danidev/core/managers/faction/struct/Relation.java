package me.danidev.core.managers.faction.struct;

import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Relation {
	
    MEMBER("MEMBER", 0, 3), 
    ALLY("ALLY", 1, 2), 
    ENEMY("ENEMY", 2, 1);
    
    private final int value;
    
    private Relation(final String s, final int n, final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public boolean isAtLeast(final Relation relation) {
        return this.value >= relation.value;
    }
    
    public boolean isAtMost(final Relation relation) {
        return this.value <= relation.value;
    }
    
    public boolean isMember() {
        return this == Relation.MEMBER;
    }
    
    public boolean isAlly() {
        return this == Relation.ALLY;
    }
    
    public boolean isEnemy() {
        return this == Relation.ENEMY;
    }
    
    public String getDisplayName() {
        if (this == Relation.ALLY) {
            return this.toChatColour() + "alliance";
        }
        return this.toChatColour() + this.name().toLowerCase();
    }
    
    public ChatColor toChatColour() {
        switch (this) {
            case MEMBER: {
                return ConfigurationService.TEAMMATE_COLOR;
            }
            case ALLY: {
                return ConfigurationService.ALLY_COLOR;
            }
            default: {
                return ConfigurationService.ENEMY_COLOR;
            }
        }
    }
    
    public DyeColor toDyeColour() {
        return BukkitUtils.toDyeColor(this.toChatColour());
    }
}
