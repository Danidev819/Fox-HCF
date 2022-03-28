package me.danidev.core.managers.classes.rogue;

import me.danidev.core.Main;
import me.danidev.core.managers.classes.bard.BardClass;
import me.danidev.core.managers.classes.event.PvPClassUnEquipEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import com.google.common.collect.HashBasedTable;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.UUID;
import com.google.common.collect.Table;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;

public class RogueRestorer implements Listener {
	
    private Table<UUID, PotionEffectType, PotionEffect> restores;
    
    public RogueRestorer(Main plugin) {
        this.restores = HashBasedTable.create();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPvpClassUnequip(PvPClassUnEquipEvent event) {
        this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }
    
    public void setRestoreEffect(Player player, PotionEffect effect) {
        if (effect == null) {
            return;
        }
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = (Collection<PotionEffect>)player.getActivePotionEffects();
        for (PotionEffect active : activeList) {
            if (active.getType().equals(effect.getType())) {
                if (effect.getAmplifier() < active.getAmplifier()) {
                    return;
                }
                if (effect.getAmplifier() == active.getAmplifier() && effect.getDuration() < active.getDuration()) {
                    return;
                }
                this.restores.put(player.getUniqueId(), active.getType(), active);
                shouldCancel = false;
            }
        }
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > 100 && effect.getDuration() < BardClass.DEFAULT_MAX_DURATION) {
            this.restores.remove(player.getUniqueId(), effect.getType());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player)livingEntity;
            PotionEffect previous = this.restores.remove(player.getUniqueId(), event.getEffect().getType());
            if (previous != null) {
                event.setCancelled(true);
                new BukkitRunnable() {
                    public void run() {
                        player.addPotionEffect(previous, true);
                    }
                }.runTask(Main.get());
            }
        }
    }
}
