package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.TaskUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SwapperAxe extends Ability {

    private final Main plugin = Main.get();

    public SwapperAxe() {
        super("SWAPPER_AXE");
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

            if (Main.get().getSwapperAxe().onCooldown(damager)) {
                damager.sendMessage(CC.translate("&bYou are on &6&lSwapper Axe &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getSwapperAxe().getRemainingMilis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(damager)){
                damager.sendMessage(CC.translate("&bYou are on &d&lPartner Item &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            Player victim = (Player) event.getEntity();
            ItemStack helmet = victim.getInventory().getHelmet();

            if (helmet == null || !helmet.getType().equals(Material.DIAMOND_HELMET)) return;

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

            this.onSwapperAxe(victim, damager, helmet);

            Main.get().getSwapperAxe().applyCooldown(damager, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(damager,  10 * 1000);

            plugin.getAbilityManager().cooldownExpired(damager, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(damager, this.getAbility());
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!isAbility(event.getItem())) return;

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Player player = event.getPlayer();

            if (this.hasCooldown(player)) {
                event.setCancelled(true);
                plugin.getAbilityManager().cooldown(player, this.getName(), this.getCooldown(player));
                player.updateInventory();
            }
        }
    }

    private void onSwapperAxe(Player victim, Player damager, ItemStack helmet) {
        plugin.getAbilityManager().targetMessage(victim, damager, this.getAbility());
        Faction factionAt = Main.get().getFactionManager().getFactionAt(damager.getLocation());
        Faction factionAt1 = Main.get().getFactionManager().getFactionAt(victim.getLocation());

        TaskUtils.runLaterAsync(() -> {
            victim.getInventory().addItem(helmet);
            victim.getInventory().setHelmet(null);
            victim.updateInventory();
        }, 5 * 20L);
    }
}