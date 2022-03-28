package me.danidev.core.managers.abilities.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Cooldowns;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import me.danidev.core.managers.faction.type.Faction;
import net.minecraft.util.com.google.common.collect.Maps;

public class Beacon extends Ability {

    private final Map<Location, Set<Player>> affectedPlayers;

    public Beacon() {
        super("BEACON");

        this.affectedPlayers = Maps.newLinkedHashMap();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (Cooldowns.isOnCooldown("DENY-BLOCK", player)) {
            event.setCancelled(true);
            player.sendMessage(
                    CC.translate("&cYou can't place blocks due to a &6&lBeacon &cability was activated nearest you!"));
            return;
        }

        if (!isAbility(player.getItemInHand())) {
            return;
        }

        Block block = event.getBlock();
        Faction factionAt = Main.get().getFactionManager().getFactionAt(block.getLocation());

        if (factionAt.isSafezone()) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
            return;
        }

        if (Main.get().getPartnerItem().onCooldown(player)) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&7You are on &d&lPartner Item &7cooldown for &6" + DurationFormatter
                    .getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
            return;
        }

        if (Main.get().getBeacon().onCooldown(player)) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&7You are on &6&lBeacon &7cooldown for &6"
                    + DurationFormatter.getRemaining(Main.get().getBeacon().getRemainingMilis(player), true, true)));
            return;
        }

        PlayerUtil.decrement(player);

        Main.get().getBeacon().applyCooldown(player, 60 * 1000);
        Main.get().getPartnerItem().applyCooldown(player, 10 * 1000);

        this.setAntiBuild(player, block.getLocation().clone());

        Main.get().getAbilityManager().cooldownExpired(player, this.getName(), this.getAbility());
        Main.get().getAbilityManager().playerMessage(player, this.getAbility());
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() == Material.BEACON) {
            if (this.affectedPlayers.containsKey(block.getLocation())) {
                Set<Player> affectedPlayers = this.affectedPlayers.get(block.getLocation());

                affectedPlayers.forEach(target -> {
                    Cooldowns.removeCooldown("DENY-BLOCK", target);
                });

                this.affectedPlayers.remove(block.getLocation());

                player.sendMessage(
                        CC.translate("&aYou have broken the &6&lBeacon &aability block! Now you can build again."));
                return;
            }
        }

        if (Cooldowns.isOnCooldown("DENY-BLOCK", player)) {
            event.setCancelled(true);
            player.sendMessage(
                    CC.translate("&cYou can't place blocks due to a &6&lBeacon &cability was activated nearest you!"));
        }
    }

    private void setAntiBuild(Player player, Location blockLocation) {
        Set<Player> nearbyPlayers = player.getNearbyEntities(10.0D, 10.0D, 10.0D).stream()
                .filter(entity -> entity instanceof Player).filter(nearbyPlayer -> nearbyPlayer != player)
                .map(entity -> (Player) entity).collect(Collectors.toSet());

        nearbyPlayers.forEach(nearbyPlayer -> Cooldowns.addCooldown("DENY-BLOCK", nearbyPlayer, 15));

        this.affectedPlayers.put(blockLocation, nearbyPlayers);
    }
}