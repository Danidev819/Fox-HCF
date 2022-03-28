package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.listeners.event.PlayerMessageEvent;
import me.danidev.core.managers.user.BaseUser;
import me.danidev.core.managers.user.ServerParticipator;
import me.danidev.core.utils.CC;
import me.danidev.core.handlers.ChatHandler;
import me.danidev.core.managers.faction.event.FactionChatEvent;
import me.danidev.core.managers.faction.struct.ChatChannel;
import me.danidev.core.managers.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChatListener implements Listener {

    public ChatListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = player.hasPermission("fhcf.chat.color") ? CC.translate(event.getMessage()) : event.getMessage();

        PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());
        ChatChannel chatChannel = (playerFaction == null) ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        Set<Player> recipients = event.getRecipients();

        if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
            if (!this.isGlobalChannel(message)) {
                Set<Player> online = playerFaction.getOnlinePlayers();

                if (chatChannel == ChatChannel.ALLIANCE) {
                    List<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for (PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }

                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
                return;
            }

            message = message.substring(1).trim();
            event.setMessage(message);
        }
        event.setCancelled(true);

        if (ChatHandler.isMuted() && !player.hasPermission("fhcf.staff")) {
            player.sendMessage(CC.translate("&cChat is currently muted."));
            return;
        }

        for (Player recipient : event.getRecipients()) {
            String playerName = player.getName();
            String faction = (playerFaction == null) ? "*" : playerFaction.getDisplayName(recipient);

            String rank = Main.get().getRankManager().getRank().getName(player);
            String rankPrefix = Main.get().getRankManager().getRank().getPrefix(player);
            String rankSuffix = Main.get().getRankManager().getRank().getSuffix(player);
            String rankColor = Main.get().getRankManager().getRank().getColor(player);

            String chat = CC.translate(Main.get().getMainConfig().getString("CHAT.FORMAT")
                    .replace("%FACTION%", faction)
                    .replace("%PLAYER%", playerName)
                    .replace("%RANK%", rank)
                    .replace("%RANK_PREFIX%", rankPrefix)
                    .replace("%RANK_SUFFIX%", rankSuffix)
                    .replace("%RANK_COLOR%", rankColor)
                    .replace("%MESSAGE%", message));

            recipient.sendMessage(chat);
        }

        String playerName = player.getName();
        String faction = (playerFaction == null) ? "*" : playerFaction.getDisplayName(player);

        String rank = Main.get().getRankManager().getRank().getName(player);
        String rankPrefix = Main.get().getRankManager().getRank().getPrefix(player);
        String rankSuffix = Main.get().getRankManager().getRank().getSuffix(player);
        String rankColor = Main.get().getRankManager().getRank().getColor(player);

        String chat = CC.translate(Main.get().getMainConfig().getString("CHAT.FORMAT")
                .replace("%FACTION%", faction)
                .replace("%PLAYER%", playerName)
                .replace("%RANK%", rank)
                .replace("%RANK_PREFIX%", rankPrefix)
                .replace("%RANK_SUFFIX%", rankSuffix)
                .replace("%RANK_COLOR%", rankColor)
                .replace("%MESSAGE%", message));

        Bukkit.getConsoleSender().sendMessage(chat);
    }

    private boolean isGlobalChannel(String input) {
        int length = input.length();
        if (length <= 1 || !input.startsWith("!")) {
            return false;
        }
        int i = 1;
        while (i < length) {
            char character = input.charAt(i);
            if (character == ' ') {
                ++i;
            } else {
                if (character != '/') {
                    break;
                }
                return false;
            }
        }
        return true;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player recipient = event.getRecipient();
        UUID recipientUUID = recipient.getUniqueId();

        if (!sender.hasPermission("fhcf.messaging.bypass")) {
            BaseUser recipientUser = Main.get().getUserManager().getBaseUser(recipientUUID);
            if (!recipientUser.isMessagesVisible() || recipientUser.getIgnoring().contains(sender.getName())) {
                event.setCancelled(true);
                sender.sendMessage(ChatColor.RED + recipient.getName() + " has private messaging toggled.");
            }
            return;
        }

        ServerParticipator senderParticipator = Main.get().getUserManager().getParticipator(sender);

        if (!senderParticipator.isMessagesVisible()) {
            event.setCancelled(true);
            sender.sendMessage(ChatColor.RED + "You have private messages toggled.");
        }
    }
}
