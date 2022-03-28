package me.danidev.core.utils.scoreboard.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class AssembleBoardDestroyEvent extends Event implements Cancellable
{
    public static HandlerList handlerList;
    private Player player;
    private boolean cancelled;

    public AssembleBoardDestroyEvent(final Player player) {
        this.cancelled = false;
        this.player = player;
    }

    public void setCancelled(final boolean b) {
        this.cancelled = b;
    }

    public HandlerList getHandlers() {
        return AssembleBoardDestroyEvent.handlerList;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public static HandlerList getHandlerList() {
        return AssembleBoardDestroyEvent.handlerList;
    }

    static {
        AssembleBoardDestroyEvent.handlerList = new HandlerList();
    }
}
