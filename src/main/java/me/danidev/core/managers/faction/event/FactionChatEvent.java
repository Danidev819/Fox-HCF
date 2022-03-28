package me.danidev.core.managers.faction.event;

import me.danidev.core.managers.faction.struct.ChatChannel;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.command.CommandSender;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.danidev.core.managers.faction.type.PlayerFaction;

import org.bukkit.event.Cancellable;

public class FactionChatEvent extends FactionEvent implements Cancellable
{
    private static final HandlerList handlers;
    private final Player player;
    private final FactionMember factionMember;
    private final ChatChannel chatChannel;
    private final String message;
    private final Collection<? extends CommandSender> recipients;
    private final String fallbackFormat;
    private boolean cancelled;
    
    static {
        handlers = new HandlerList();
    }
    
    public FactionChatEvent(final boolean async, final PlayerFaction faction, final Player player, final ChatChannel chatChannel, final Collection<? extends CommandSender> recipients, final String message) {
        super(faction, async);
        this.player = player;
        this.factionMember = faction.getMember(player.getUniqueId());
        this.chatChannel = chatChannel;
        this.recipients = recipients;
        this.message = message;
        this.fallbackFormat = chatChannel.getRawFormat(player);
    }
    
    public static HandlerList getHandlerList() {
        return FactionChatEvent.handlers;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public FactionMember getFactionMember() {
        return this.factionMember;
    }
    
    public ChatChannel getChatChannel() {
        return this.chatChannel;
    }
    
    public Collection<? extends CommandSender> getRecipients() {
        return this.recipients;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getFallbackFormat() {
        return this.fallbackFormat;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
    
    public HandlerList getHandlers() {
        return FactionChatEvent.handlers;
    }
}
