package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Cooldowns;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class NinjaStar extends Ability {

    private final Main plugin = Main.get();
    private final Map<UUID, UUID> TAGGED = Maps.newHashMap();

    public NinjaStar() {
        super("NINJA_STAR");
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            Cooldowns.addCooldown("TELEPORT", victim, 15);
            TAGGED.put(victim.getUniqueId(), damager.getUniqueId());
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!isAbility(event.getItem())) return;

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());

            if(factionAt.isSafezone()){
                player.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                event.setCancelled(true);
                return;
            }

            if (Main.get().getNinjaStar().onCooldown(player)) {
                player.sendMessage(CC.translate("&bYou are on &6&lNinja Star &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getNinjaStar().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(player)){
                player.sendMessage(CC.translate("&bYou are on &d&lPartner Item &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            if (!Cooldowns.isOnCooldown("TELEPORT", player)) return;

            PlayerUtil.decrement(player);

            Player target = Bukkit.getPlayer(TAGGED.get(player.getUniqueId()));

            Main.get().getEffectDisabler().applyCooldown(player, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(player,  10 * 1000);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(target.getLocation());
                    player.sendMessage(CC.translate("&3You has been succesffully teleported")); // you just got teleported back
                }
            }.runTaskLaterAsynchronously(Main.get(), (5 * 10));

            plugin.getAbilityManager().cooldownExpired(player, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(player, this.getAbility());
            plugin.getAbilityManager().targetMessage(target, player, this.getAbility());

            TAGGED.remove(player.getUniqueId());
        }
    }
}