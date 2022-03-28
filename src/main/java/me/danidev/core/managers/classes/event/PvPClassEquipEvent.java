package me.danidev.core.managers.classes.event;

import me.danidev.core.managers.classes.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PvPClassEquipEvent extends PlayerEvent
{
    private static HandlerList handlers;
    private PvPClass hCFClass;
    
    static {
        PvPClassEquipEvent.handlers = new HandlerList();
    }
    
    public PvPClassEquipEvent(final Player player, final PvPClass hCFClass) {
        super(player);
        this.hCFClass = hCFClass;
    }
    
    public PvPClass getPvpClass() {
        return this.hCFClass;
    }
    
    public static HandlerList getHandlerList() {
        return PvPClassEquipEvent.handlers;
    }
    
    public HandlerList getHandlers() {
        return PvPClassEquipEvent.handlers;
    }
}
