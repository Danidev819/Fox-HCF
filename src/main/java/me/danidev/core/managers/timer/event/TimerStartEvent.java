package me.danidev.core.managers.timer.event;

import javax.annotation.Nullable;

import java.util.UUID;

import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.Timer;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerStartEvent extends Event
{
    private static final HandlerList handlers;
    private final Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private final long duration;
    
    static {
        handlers = new HandlerList();
    }
    
    public TimerStartEvent(final Timer timer, final long duration) {
        this.player = Optional.absent();
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.duration = duration;
    }
    
    public TimerStartEvent(@Nullable final Player player, final UUID uniqueId, final PlayerTimer timer, final long duration) {
        this.player = (Optional<Player>)Optional.fromNullable(player);
        this.userUUID = (Optional<UUID>)Optional.fromNullable(uniqueId);
        this.timer = timer;
        this.duration = duration;
    }
    
    public static HandlerList getHandlerList() {
        return TimerStartEvent.handlers;
    }
    
    public Optional<Player> getPlayer() {
        return this.player;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public HandlerList getHandlers() {
        return TimerStartEvent.handlers;
    }
}
