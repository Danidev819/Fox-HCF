package me.danidev.core.managers.faction.type;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.event.FactionRenameEvent;
import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.utils.service.ConfigurationService;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import java.util.LinkedHashMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@Getter
public abstract class Faction implements ConfigurationSerializable {

    protected UUID uniqueID;
    public long lastRenameMillis;
    protected String name;
    protected long creationMillis;
    protected double dtrLossWorldMultiplier;
    protected double dtrLossNetherMultiplier;
    protected double dtrLossEndMultiplier;
    protected double dtrLossDeathRoom;
    protected double deathbanMultiplier;
    protected boolean safezone;
    protected boolean locked;
    
    public Faction(String name) {
        this.dtrLossWorldMultiplier = ConfigurationService.DTR_LOSS_WORLD;
        this.dtrLossNetherMultiplier = ConfigurationService.DTR_LOSS_NETHER;
        this.dtrLossEndMultiplier = ConfigurationService.DTR_LOSS_END;
        this.dtrLossDeathRoom = ConfigurationService.DTR_LOSS_DEATH_ROOM;
        this.deathbanMultiplier = 0.1;
        this.uniqueID = UUID.randomUUID();
        this.name = name;
    }
    
    public Faction(Map<String, Object> map) {
        this.dtrLossWorldMultiplier = ConfigurationService.DTR_LOSS_WORLD;
        this.dtrLossNetherMultiplier = ConfigurationService.DTR_LOSS_NETHER;
        this.dtrLossEndMultiplier = ConfigurationService.DTR_LOSS_END;
        this.deathbanMultiplier = 0.1;
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.name = (String) map.get("name");
        this.creationMillis = Long.parseLong((String) map.get("creationMillis"));
        this.lastRenameMillis = Long.parseLong((String) map.get("lastRenameMillis"));
        this.deathbanMultiplier = (double)map.get("deathbanMultiplier");
        this.safezone = (boolean)map.get("safezone");
    }
    
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("name", this.name);
        map.put("creationMillis", Long.toString(this.creationMillis));
        map.put("lastRenameMillis", Long.toString(this.lastRenameMillis));
        map.put("deathbanMultiplier", this.deathbanMultiplier);
        map.put("safezone", this.safezone);
        return map;
    }
    
    public UUID getUniqueID() {
        return this.uniqueID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean setName(String name) {
        return this.setName(name, Bukkit.getConsoleSender());
    }
    
    public boolean setName(String name, CommandSender sender) {
        if (this.name.equals(name)) {
            return false;
        }

        FactionRenameEvent event = new FactionRenameEvent(this, sender, this.name, name);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        this.lastRenameMillis = System.currentTimeMillis();
        this.name = name;

        return true;
    }
    
    public Relation getFactionRelation(Faction faction) {
        if (faction == null) {
            return Relation.ENEMY;
        }
        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;
            if (playerFaction.equals(this)) {
                return Relation.MEMBER;
            }
            if (playerFaction.getAllied().contains(this.uniqueID)) {
                return Relation.ALLY;
            }
        }
        return Relation.ENEMY;
    }
    
    public Relation getRelation(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return Relation.ENEMY;
        }
        Player player = (Player) sender;
        return this.getFactionRelation(Main.get().getFactionManager().getPlayerFaction(player.getUniqueId()));
    }
    
    public String getDisplayName(CommandSender sender) {
        return (this.safezone ? ConfigurationService.SAFEZONE_COLOR : this.getRelation(sender).toChatColour()) + this.name;
    }
    
    public String getDisplayName(Faction other) {
        return this.getFactionRelation(other).toChatColour() + this.name;
    }
    
    public void printDetails(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
        sender.sendMessage(" " + this.getDisplayName(sender));
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
    }
    
    public boolean isDeathban() {
        return !this.safezone && this.deathbanMultiplier > 0.0;
    }
    
    public void setDeathban(boolean deathban) {
        if (deathban != this.isDeathban()) {
            this.deathbanMultiplier = (deathban ? 1.0 : 0.0);
        }
    }
    
    public double getDeathbanMultiplier() {
        return this.deathbanMultiplier;
    }
    
    public void setDeathbanMultiplier(double deathbanMultiplier) {
        Preconditions.checkArgument(deathbanMultiplier >= 0.0, "Deathban multiplier may not be negative");
        this.deathbanMultiplier = deathbanMultiplier;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public boolean isSafezone() {
        return this.safezone;
    }
}
