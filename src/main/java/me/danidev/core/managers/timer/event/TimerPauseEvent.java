package me.danidev.core.managers.timer.event;

import java.util.UUID;
import com.google.common.base.Optional;

import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.Timer;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class TimerPauseEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private final boolean paused;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private boolean cancelled;
    
    static {
        handlers = new HandlerList();
    }
    
    public TimerPauseEvent(final Timer timer, final boolean paused) {
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.paused = paused;
    }
    
    public TimerPauseEvent(final UUID userUUID, final PlayerTimer timer, final boolean paused) {
        this.userUUID = (Optional<UUID>)Optional.fromNullable(userUUID);
        this.timer = timer;
        this.paused = paused;
    }
    
    public static HandlerList getHandlerList() {
        return TimerPauseEvent.handlers;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public HandlerList getHandlers() {
        return TimerPauseEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
