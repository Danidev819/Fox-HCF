package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Scrammbler extends Ability {

    private final Main plugin = Main.get();

    public Scrammbler() {
        super("SCRAMMBLER");
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            if (!isAbility(damager.getItemInHand())) return;

            Faction factionAt = Main.get().getFactionManager().getFactionAt(damager.getLocation());

            if(factionAt.isSafezone()){
                damager.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                event.setCancelled(true);
                return;
            }

            if (Main.get().getScrammbler().onCooldown(damager)) {
                damager.sendMessage(CC.translate("&bYou are on &6&lScrammbler &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getScrammbler().getRemainingMilis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(damager)){
                damager.sendMessage(CC.translate("&bYou are on &d&lPartner Item &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            PlayerUtil.decrement(damager);

            Player victim = (Player) event.getEntity();

            Faction factionAt1 = Main.get().getFactionManager().getFactionAt(victim.getLocation());
            PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(damager.getUniqueId());
            PlayerFaction victimFaction = Main.get().getFactionManager().getPlayerFaction(victim.getUniqueId());

            if(playerFaction.equals(victimFaction)) {
                damager.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                return;
            }

            if(factionAt1.isSafezone()){
                damager.sendMessage(CC.translate("&cYou can´t use this ability against players who are in safezone"));
                event.setCancelled(true);
                return;
            }

            Main.get().getScrammbler().applyCooldown(damager, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(damager,  10 * 1000);
            this.random(victim);

            plugin.getAbilityManager().cooldownExpired(damager, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(damager, this.getAbility());
            plugin.getAbilityManager().targetMessage(victim, damager, this.getAbility());
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isAbility(event.getItem())) return;

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            event.setCancelled(true);

            if (this.hasCooldown(player)) {
                plugin.getAbilityManager().cooldown(player, this.getName(), this.getCooldown(player));
                player.updateInventory();
            }
        }
    }

    private void random(Player victim) {
        Inventory victimInventory = victim.getInventory();

        ItemStack slot1 = victimInventory.getItem(0);
        ItemStack slot2 = victimInventory.getItem(1);
        ItemStack slot3 = victimInventory.getItem(2);
        ItemStack slot4 = victimInventory.getItem(3);
        ItemStack slot5 = victimInventory.getItem(4);
        ItemStack slot6 = victimInventory.getItem(5);
        ItemStack slot7 = victimInventory.getItem(6);
        ItemStack slot8 = victimInventory.getItem(7);
        ItemStack slot9 = victimInventory.getItem(8);

        victimInventory.setItem(0, slot4);
        victimInventory.setItem(1, slot3);
        victimInventory.setItem(2, slot6);
        victimInventory.setItem(3, slot8);
        victimInventory.setItem(4, slot9);
        victimInventory.setItem(5, slot1);
        victimInventory.setItem(6, slot2);
        victimInventory.setItem(7, slot5);
        victimInventory.setItem(8, slot7);

        victim.updateInventory();
    }
}
