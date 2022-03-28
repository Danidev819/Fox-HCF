package me.danidev.core.listeners.fixes;

import java.util.Collection;

import me.danidev.core.Main;
import me.danidev.core.managers.classes.PvPClass;
import me.danidev.core.managers.classes.others.ArcherClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ArcherBeaconSpeedFixListener implements Listener {

    private Main plugin;

    public ArcherBeaconSpeedFixListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        PvPClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);

        if(pvpClass instanceof ArcherClass)
        {
            Collection<PotionEffect> pe = player.getActivePotionEffects();
            for(PotionEffect effect : pe)
            {
                if(effect.getType().equals(PotionEffectType.SPEED))
                {
                    if(effect.getAmplifier() == 1)
                    {
                        player.removePotionEffect(PotionEffectType.SPEED);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                    }
                }
            }
        }
    }
}