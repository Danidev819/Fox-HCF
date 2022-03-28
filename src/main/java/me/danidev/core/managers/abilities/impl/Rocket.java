package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class Rocket extends Ability {

    private HashSet<Player> nofall;

    public Rocket() {
        super("ROCKET");
    }

    @EventHandler
    public void onItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!isAbility(player.getItemInHand())) {
                return;
            }

            if(factionAt.isSafezone()){
                player.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                event.setCancelled(true);
                return;
            }

            if (Main.get().getRocket().onCooldown(player)) {
                player.sendMessage(CC.translate("&bYou are on &6&lRocket &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getRocket().getRemainingMilis(player), true, true)));
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

            if (isAbility(player.getItemInHand())) {
                player.setVelocity(new Vector(0.1D, 2.0D, 0.0D));

                PlayerUtil.decrement(player);

                Main.get().getRocket().applyCooldown(player,  60 * 1000);
                Main.get().getPartnerItem().applyCooldown(player, 10 * 1000);
                player.setMetadata("rocket", new FixedMetadataValue(Main.get(), true));
            }
        }
    }

    @EventHandler
    public void fallDamage(final EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            final Player player = (Player)event.getEntity();
            if (Main.get().getRocket().onCooldown(player)) {
                event.setCancelled(true);
            }
        }
    }
}
