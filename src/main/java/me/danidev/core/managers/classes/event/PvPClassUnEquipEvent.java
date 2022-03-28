package me.danidev.core.managers.classes.event;

import me.danidev.core.managers.classes.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PvPClassUnEquipEvent extends PlayerEvent
{
    private static final HandlerList handlers;
    private final PvPClass pvpClass;
    
    static {
        handlers = new HandlerList();
    }
    
    public PvPClassUnEquipEvent(final Player player, final PvPClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }
    
    public static HandlerList getHandlerList() {
        return PvPClassUnEquipEvent.handlers;
    }
    
    public PvPClass getPvpClass() {
        return this.pvpClass;
    }
    
    public HandlerList getHandlers() {
        return PvPClassUnEquipEvent.handlers;
    }
}
