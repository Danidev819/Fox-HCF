package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import com.google.common.collect.Sets;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.UUID;

public class GuardianAngel extends Ability {

    private final Main plugin = Main.get();
    private final Set<UUID> guardians = Sets.newHashSet();

    public GuardianAngel() {
        super("GUARDIAN_ANGEL");
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!isAbility(event.getItem())) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());

            if(factionAt.isSafezone()){
                player.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                event.setCancelled(true);
                return;
            }

            if (Main.get().getGuardianAngel().onCooldown(player)) {
                player.sendMessage(CC.translate("&7You are on &6&lGuardian Angel &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getGuardianAngel().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(player)){
                player.sendMessage(CC.translate("&7You are on &d&lPartner Item &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            PlayerUtil.decrement(player);

            Main.get().getGuardianAngel().applyCooldown(player, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(player,  10 * 1000);
            guardians.add(player.getUniqueId());

            plugin.getAbilityManager().playerMessage(player, this.getAbility());
            plugin.getAbilityManager().cooldownExpired(player, this.getName(), this.getAbility());
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (guardians.contains(event.getEntity().getUniqueId())) {
                Player player = (Player) event.getEntity();
                if (player.getHealth() < 4.1 || player.getHealth() == 3.0) {
                    player.setHealth(player.getMaxHealth());
                    player.playSound(player.getLocation(), Sound.DRINK, 1F, 1F);
                    guardians.remove(player.getUniqueId());
                }
            }
        }
    }
}