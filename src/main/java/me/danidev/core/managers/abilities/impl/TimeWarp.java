package me.danidev.core.managers.abilities.impl;

import java.util.Map;
import java.util.UUID;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.com.google.common.collect.Maps;

public class TimeWarp extends Ability {

    private final Map<UUID, Map<Location, Long>> lastPearl;
    private final long PEARL_EXPIRE = 15 * 1000;

    public TimeWarp() {
        super("TIME_WARP");

        this.lastPearl = Maps.newConcurrentMap();

        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.get(), () -> {

            for (Map.Entry<UUID, Map<Location, Long>> lastPearlEntry : this.lastPearl.entrySet()) {
                UUID uuid = lastPearlEntry.getKey();
                Map<Location, Long> map = lastPearlEntry.getValue();

                if (System.currentTimeMillis() > map.values().iterator().next()) {
                    this.lastPearl.remove(uuid);
                }
            }

        }, 20L, 20L);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntityType() != EntityType.ENDER_PEARL) {
            return;
        }

        EnderPearl enderpearl = (EnderPearl) event.getEntity();

        if (!(enderpearl.getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) enderpearl.getShooter();
        Map<Location, Long> map = ImmutableMap.of(shooter.getLocation().clone(),
                System.currentTimeMillis() + this.PEARL_EXPIRE);

        this.lastPearl.put(shooter.getUniqueId(), map);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().name().contains("RIGHT_CLICK")) {
            return;
        }

        if (!isAbility(event.getItem())) {
            return;
        }

        if (Main.get().getTimeWarp().onCooldown(player)) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&bYou are on &6&lTime Warp &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getTimeWarp().getRemainingMilis(player), true, true)));
            player.updateInventory();
            event.setCancelled(true);
            return;
        }

        if (Main.get().getPartnerItem().onCooldown(player)) {
            player.sendMessage(CC.translate("&bYou are on &d&lPartner Item &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
            player.updateInventory();
            event.setCancelled(true);
            return;
        }

        if (!this.lastPearl.containsKey(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYour last enderpearl location has expired!"));
            return;
        }

        Location location = this.lastPearl.get(player.getUniqueId()).keySet().iterator().next().clone();

        PlayerUtil.decrement(player);

        Main.get().getTimeWarp().applyCooldown(player, 60 * 1000);
        Main.get().getPartnerItem().applyCooldown(player, 10 * 1000);

        player.sendMessage(CC.translate(
                "&bYou &aactivated &ba Time Warp, so you will be teleported to your last thrown enderpearl's location in &33 &bseconds!"));

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            player.teleport(location);
            player.sendMessage(
                    CC.translate("&bYou have been &ateleported &bto your last thrown enderpearl's location!"));

            this.lastPearl.remove(player.getUniqueId());
        }, 60L);

        Main.get().getAbilityManager().cooldownExpired(player, this.getName(), this.getAbility());
        Main.get().getAbilityManager().playerMessage(player, this.getAbility());
    }
}