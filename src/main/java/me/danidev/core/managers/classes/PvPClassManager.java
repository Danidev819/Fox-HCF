package me.danidev.core.managers.classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import me.danidev.core.Main;
import me.danidev.core.managers.classes.bard.BardClass;
import me.danidev.core.managers.classes.event.PvPClassEquipEvent;
import me.danidev.core.managers.classes.event.PvPClassUnEquipEvent;
import me.danidev.core.managers.classes.mage.MageClass;
import me.danidev.core.managers.classes.others.ArcherClass;
import me.danidev.core.managers.classes.others.MinerClass;
import me.danidev.core.managers.classes.rogue.RogueClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class PvPClassManager {
	
    private final Map<UUID, PvPClass> equippedClass;
    private final Map<String, PvPClass> pvpClasses;
    
    public PvPClassManager(final Main plugin) {
        this.equippedClass = new HashMap<>();
        this.pvpClasses = new HashMap<>();

        this.pvpClasses.put("Archer", new ArcherClass(plugin));
        this.pvpClasses.put("Bard", new BardClass(plugin));
        this.pvpClasses.put("Miner", new MinerClass(plugin));
        this.pvpClasses.put("Rogue", new RogueClass(plugin));
        this.pvpClasses.put("Mage", new MageClass(plugin));

        for (PvPClass pvpClass : this.pvpClasses.values()) {

            if (!(pvpClass instanceof Listener)) continue;

            plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, plugin);
        }
    }
    
    public void onDisable() {
    	for(UUID uuid : equippedClass.keySet()) {
    		Player player = Bukkit.getPlayer(uuid);
    		if(player != null) {
    			setEquippedClass(player, null);
    		}
    	}
    	
        this.pvpClasses.clear();
        this.equippedClass.clear();
    }
    
    public Collection<PvPClass> getPvpClasses() {
        return this.pvpClasses.values();
    }
    
    public PvPClass getPvpClass(final String name) {
        return this.pvpClasses.get(name);
    }
    
    public PvPClass getEquippedClass(final Player player) {
        final Map<UUID, PvPClass> map = this.equippedClass;
        synchronized (map) {
            return this.equippedClass.get(player.getUniqueId());
        }
    }
    
    public boolean hasClassEquipped(final Player player, final PvPClass pvpClass) {
        final PvPClass equipped = this.getEquippedClass(player);
        return equipped != null && equipped.equals(pvpClass);
    }
    
    public void setEquippedClass(final Player player, @Nullable final PvPClass pvpClass) {
        final PvPClass equipped = this.getEquippedClass(player);
        if (equipped != null) {
            if (pvpClass == null) {
                this.equippedClass.remove(player.getUniqueId());
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent(new PvPClassUnEquipEvent(player, equipped));
                return;
            }
        }
        else if (pvpClass == null) {
            return;
        }
        if (pvpClass.onEquip(player)) {
            this.equippedClass.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent(new PvPClassEquipEvent(player, pvpClass));
        }
    }
}
