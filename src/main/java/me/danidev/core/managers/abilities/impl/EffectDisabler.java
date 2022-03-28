package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class EffectDisabler extends Ability {

    private final Main plugin = Main.get();

    private final Map<UUID, Integer> HITS = Maps.newHashMap();

    public EffectDisabler() {
        super("EFFECT_DISABLER");
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            if (!isAbility(damager.getItemInHand())) return;

            if (isBard(victim) || isArcher(victim) || isRogue(victim) || isMiner(victim)) return;

            Faction factionAt = Main.get().getFactionManager().getFactionAt(damager.getLocation());

            PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(damager.getUniqueId());
            PlayerFaction victimFaction = Main.get().getFactionManager().getPlayerFaction(victim.getUniqueId());
            if(playerFaction.equals(victimFaction)) {
                damager.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                return;
            }

            if(factionAt.isSafezone()){
                damager.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                event.setCancelled(true);
                return;
            }

            if (Main.get().getEffectDisabler().onCooldown(damager)) {
                damager.sendMessage(CC.translate("&7You are on &6&lEffect Disabler &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getEffectDisabler().getRemainingMilis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(damager)){
                damager.sendMessage(CC.translate("&7You are on &d&lPartner Item &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            if (!HITS.containsKey(victim.getUniqueId())) {
                HITS.put(victim.getUniqueId(), 0);
            }

            HITS.put(victim.getUniqueId(), HITS.get(victim.getUniqueId()) + 1);

            if (HITS.get(victim.getUniqueId()) != 5) return;

            PlayerUtil.decrement(damager);

            Main.get().getEffectDisabler().applyCooldown(damager, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(damager,  10 * 1000);
            HITS.remove(victim.getUniqueId());

            victim.getActivePotionEffects().forEach(potionEffect -> victim.removePotionEffect(potionEffect.getType()));

            plugin.getAbilityManager().cooldownExpired(damager, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(damager, this.getAbility());
            plugin.getAbilityManager().targetMessage(victim, damager, this.getAbility());
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!isAbility(event.getItem())) return;

        event.setCancelled(true);

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Player player = event.getPlayer();

            if (this.hasCooldown(player)) {
                event.setCancelled(true);
                plugin.getAbilityManager().cooldown(player, this.getName(), this.getCooldown(player));
                player.updateInventory();
            }
        }
    }

    private boolean isBard(Player victim) {
        return victim.getInventory().getHelmet() != null && victim.getInventory().getHelmet().getType().equals(Material.GOLD_HELMET)
                && victim.getInventory().getChestplate() != null && victim.getInventory().getChestplate().getType().equals(Material.GOLD_CHESTPLATE)
                && victim.getInventory().getLeggings() != null && victim.getInventory().getLeggings().getType().equals(Material.GOLD_LEGGINGS)
                && victim.getInventory().getBoots() != null && victim.getInventory().getBoots().getType().equals(Material.GOLD_BOOTS);
    }

    private boolean isArcher(Player victim) {
        return victim.getInventory().getHelmet() != null && victim.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET)
                && victim.getInventory().getChestplate() != null && victim.getInventory().getChestplate().getType().equals(Material.LEATHER_CHESTPLATE)
                && victim.getInventory().getLeggings() != null && victim.getInventory().getLeggings().getType().equals(Material.LEATHER_LEGGINGS)
                && victim.getInventory().getBoots() != null && victim.getInventory().getBoots().getType().equals(Material.LEATHER_BOOTS);
    }

    private boolean isRogue(Player victim) {
        return victim.getInventory().getHelmet() != null && victim.getInventory().getHelmet().getType().equals(Material.CHAINMAIL_HELMET)
                && victim.getInventory().getChestplate() != null && victim.getInventory().getChestplate().getType().equals(Material.CHAINMAIL_CHESTPLATE)
                && victim.getInventory().getLeggings() != null && victim.getInventory().getLeggings().getType().equals(Material.CHAINMAIL_LEGGINGS)
                && victim.getInventory().getBoots() != null && victim.getInventory().getBoots().getType().equals(Material.CHAINMAIL_BOOTS);
    }

    private boolean isMiner(Player victim) {
        return victim.getInventory().getHelmet() != null && victim.getInventory().getHelmet().getType().equals(Material.IRON_HELMET)
                && victim.getInventory().getChestplate() != null && victim.getInventory().getChestplate().getType().equals(Material.IRON_CHESTPLATE)
                && victim.getInventory().getLeggings() != null && victim.getInventory().getLeggings().getType().equals(Material.IRON_LEGGINGS)
                && victim.getInventory().getBoots() != null && victim.getInventory().getBoots().getType().equals(Material.IRON_BOOTS);
    }
}