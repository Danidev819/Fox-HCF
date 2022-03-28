package me.danidev.core.managers.faction.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;

import me.danidev.core.managers.faction.event.cause.FactionLeaveCause;
import me.danidev.core.managers.faction.type.PlayerFaction;

import java.util.UUID;
import org.bukkit.event.HandlerList;

public class PlayerLeftFactionEvent extends FactionEvent
{
    private static final HandlerList handlers;
    private final UUID uniqueID;
    private final FactionLeaveCause cause;
    private Optional<Player> player;
    
    static {
        handlers = new HandlerList();
    }
    
    public PlayerLeftFactionEvent(final Player player, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
        this.cause = cause;
    }
    
    public PlayerLeftFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        this.uniqueID = playerUUID;
        this.cause = cause;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLeftFactionEvent.handlers;
    }
    
    @Override
    public PlayerFaction getFaction() {
        return (PlayerFaction)super.getFaction();
    }
    
    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = (Optional<Player>)Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }
    
    public UUID getUniqueID() {
        return this.uniqueID;
    }
    
    public FactionLeaveCause getCause() {
        return this.cause;
    }
    
    public HandlerList getHandlers() {
        return PlayerLeftFactionEvent.handlers;
    }
}
