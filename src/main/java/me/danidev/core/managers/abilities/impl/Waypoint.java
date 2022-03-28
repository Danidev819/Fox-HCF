package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.ParticleEffect;
import me.danidev.core.utils.PlayerUtil;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.*;
import org.bukkit.*;
import org.bukkit.plugin.*;

public class Waypoint extends Ability {

    private Main plugin = Main.get();
    private ItemStack prePearlItem;

    public Waypoint() {
        super("WAYPOINT");
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        final Location location = player.getLocation();
        Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());

        if (!isAbility(playerInteractEvent.getItem())) return;

        if(factionAt.isSafezone()){
            player.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
            playerInteractEvent.setCancelled(true);
            return;
        }

        if (Main.get().getWaypoint().onCooldown(player)) {
            player.sendMessage(CC.translate("&bYou are on &6&lWaypoint &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getWaypoint().getRemainingMilis(player), true, true)));
            player.updateInventory();
            playerInteractEvent.setCancelled(true);
            return;
        }

        if (Main.get().getPartnerItem().onCooldown(player)) {
            player.sendMessage(CC.translate("&bYou are on &d&lPartner Item &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
            player.updateInventory();
            playerInteractEvent.setCancelled(true);
            return;
        }

        PlayerUtil.decrement(player);

        Main.get().getPartnerItem().applyCooldown(player,  10 * 1000);
        Main.get().getWaypoint().applyCooldown(player, 60 * 1000);

        new BukkitRunnable() {
            public void run() {
                player.teleport(location);
                player.sendMessage(CC.translate(Main.get().getAbilitiesConfig().getString("WAYPOINT.ON-TELEPORT")));
            }
        }.runTaskLater((Plugin) Main.get(), (long)(Main.get().getAbilitiesConfig().getInt("WAYPOINT.TELEPORT-COOLDOWN") * 20));
        ParticleEffect.HUGE_EXPLODE.display(player, location.clone().add(0.5, 0.5, 0.5), 0.01f, 1);
    }
}