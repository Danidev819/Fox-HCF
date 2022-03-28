package me.danidev.core.listeners.event;

import me.danidev.core.Main;
import me.danidev.core.commands.message.TogglePMCommand;
import me.danidev.core.managers.user.BaseUser;
import me.danidev.core.utils.CC;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;
import java.util.UUID;

public class PlayerMessageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player sender;
    private final Player recipient;
    private final String message;
    private final boolean isReply;
    private boolean cancelled;

    public PlayerMessageEvent(Player sender, Set<Player> recipients, String message, boolean isReply, UUID uuid) {
        this.cancelled = false;
        this.sender = sender;
        this.recipient = Iterables.getFirst(recipients, null);
        this.message = message;
        this.isReply = isReply;
    }

    public static HandlerList getHandlerList() {
        return PlayerMessageEvent.handlers;
    }

    public Player getSender() {
        return this.sender;
    }

    public Player getRecipient() {
        return this.recipient;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isReply() {
        return this.isReply;
    }

    public void send() {
        Preconditions.checkNotNull(sender, "The sender cannot be null");
        Preconditions.checkNotNull(recipient, "The recipient cannot be null");

        BaseUser sendingUser = Main.get().getUserManager().getBaseUser(sender.getUniqueId());
        BaseUser recipientUser = Main.get().getUserManager().getBaseUser(recipient.getUniqueId());
        
        sendingUser.setLastRepliedTo(recipientUser.getUniqueId());
        recipientUser.setLastRepliedTo(sendingUser.getUniqueId());
        
        long millis = System.currentTimeMillis();

        String senderName = sender.getDisplayName();
        String recipientName = recipient.getDisplayName();

        if (TogglePMCommand.MSG.contains(recipient.getUniqueId()) && !sender.hasPermission("fhcf.staff")) {
            sender.sendMessage(CC.translate(Main.get().getLangConfig().getString("TOGGLE_PM.BLOCKED")));
            return;
        }

        sender.sendMessage(ChatColor.GRAY + "(" + ChatColor.GRAY + "To " + recipientName + ChatColor.GRAY + ") " + ChatColor.GRAY + message);
        recipient.sendMessage(ChatColor.GRAY + "(" + ChatColor.GRAY + "From " + senderName + ChatColor.GRAY + ") " + ChatColor.GRAY + message);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerMessageEvent.handlers;
    }
}
