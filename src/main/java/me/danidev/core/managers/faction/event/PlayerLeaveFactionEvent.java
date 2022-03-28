package me.danidev.core.managers.faction.event;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import me.danidev.core.managers.faction.event.cause.FactionLeaveCause;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class PlayerLeaveFactionEvent extends FactionEvent implements Cancellable
{
    private static final HandlerList handlers;
    private final UUID uniqueID;
    private final FactionLeaveCause cause;
    private boolean cancelled;
    private Optional<Player> player;
    
    static {
        handlers = new HandlerList();
    }
    
    public PlayerLeaveFactionEvent(final Player player, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull(player, "Player cannot be null");
        Preconditions.checkNotNull(playerFaction, "Player faction cannot be null");
        Preconditions.checkNotNull("Leave cause cannot be null");
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
        this.cause = cause;
    }
    
    public PlayerLeaveFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull(playerUUID, "Player UUID cannot be null");
        Preconditions.checkNotNull(playerFaction, "Player faction cannot be null");
        Preconditions.checkNotNull("Leave cause cannot be null");
        this.uniqueID = playerUUID;
        this.cause = cause;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLeaveFactionEvent.handlers;
    }
    
    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }
    
    public UUID getUniqueID() {
        return this.uniqueID;
    }
    
    public FactionLeaveCause getLeaveCause() {
        return this.cause;
    }
    
    public HandlerList getHandlers() {
        return PlayerLeaveFactionEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
