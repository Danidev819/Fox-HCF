package me.danidev.core.managers.faction;

import me.danidev.core.managers.faction.struct.ChatChannel;
import me.danidev.core.managers.faction.struct.Role;
import com.google.common.base.Preconditions;
import net.minecraft.util.com.google.common.base.Enums;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import java.util.LinkedHashMap;
import com.google.common.collect.Maps;

import java.util.Map;
import org.bukkit.entity.Player;

import java.util.UUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class FactionMember implements ConfigurationSerializable {

    private final UUID uniqueID;
    private ChatChannel chatChannel;
    private Role role;

    public FactionMember(Player player, ChatChannel chatChannel, Role role) {
        this.uniqueID = player.getUniqueId();
        this.chatChannel = chatChannel;
        this.role = role;
    }

    public FactionMember(Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.chatChannel = Enums.getIfPresent(ChatChannel.class, (String)map.get("chatChannel")).or(ChatChannel.PUBLIC);
        this.role = Enums.getIfPresent(Role.class, (String)map.get("role")).or(Role.MEMBER);
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("chatChannel", this.chatChannel.name());
        map.put("role", this.role.name());
        return map;
    }

    public String getName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.uniqueID);
        return (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) ? offlinePlayer.getName() : null;
    }

    public UUID getUniqueId() {
        return this.uniqueID;
    }

    public ChatChannel getChatChannel() {
        return this.chatChannel;
    }

    public void setChatChannel(ChatChannel chatChannel) {
        Preconditions.checkNotNull(chatChannel, "ChatChannel cannot be null");
        this.chatChannel = chatChannel;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Player toOnlinePlayer() {
        return Bukkit.getPlayer(this.uniqueID);
    }
}
