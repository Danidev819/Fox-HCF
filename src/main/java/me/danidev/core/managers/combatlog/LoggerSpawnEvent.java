package me.danidev.core.managers.combatlog;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoggerSpawnEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private LoggerEntity loggerEntity;

    public LoggerSpawnEvent(LoggerEntity loggerEntity) {
        loggerEntity = loggerEntity;
    }

    public static HandlerList getHandlerList() {
        return LoggerSpawnEvent.handlers;
    }

    public LoggerEntity getLoggerEntity() {
        return loggerEntity;
    }

    public HandlerList getHandlers() {
        return LoggerSpawnEvent.handlers;
    }
}