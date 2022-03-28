package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Cookie extends Ability {

    private final Main plugin = Main.get();

    public Cookie() {
        super("COOKIE");
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

            if (Main.get().getCookie().onCooldown(player)) {
                player.sendMessage(CC.translate("&7You are on &6&lCookie &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getCookie().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(player)){
                player.sendMessage(CC.translate("&7You are on &d&lPartner Item &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            PlayerUtil.decrement(player);

            Main.get().getCookie().applyCooldown(player, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(player,  10 * 1000);

            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 7, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 7, 4));

            plugin.getAbilityManager().cooldownExpired(player, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(player, this.getAbility());
        }
    }
}
