package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import me.danidev.core.utils.TaskUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Combo extends Ability {

    private final Main plugin = Main.get();
    private final Set<UUID> COMBO = Sets.newHashSet();
    private final Map<UUID, Integer> HITS = Maps.newHashMap();

    public Combo() {
        super("COMBO");
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

            if (Main.get().getCombo().onCooldown(player)) {
                player.sendMessage(CC.translate("&7You are on &6&lCombo &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getCombo().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(player)){
                player.sendMessage(CC.translate("&7You are on &d&lPartner Item &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            PlayerUtil.decrement(player);

            Main.get().getCombo().applyCooldown(player, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(player,  10 * 1000);
            this.giveComboEffects(player);

            COMBO.add(player.getUniqueId());
            HITS.put(player.getUniqueId(), 0);

            plugin.getAbilityManager().cooldownExpired(player, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(player, this.getAbility());
        }
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            if (COMBO.contains(damager.getUniqueId())) {
                if (HITS.containsKey(damager.getUniqueId())) {
                    HITS.put(damager.getUniqueId(), HITS.get(damager.getUniqueId()) + 1);
                }
            }
        }
    }

    private void giveComboEffects(Player player) {
        TaskUtils.runLater(() -> {
            int hits = HITS.get(player.getUniqueId());

            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * hits, 1));
            player.playSound(player.getLocation(), Sound.ZOMBIE_INFECT, 1F, 1F);
            CC.message(player, "&aYour receive Strength II for " + hits + " seconds.");

            HITS.remove(player.getUniqueId());
            COMBO.remove(player.getUniqueId());
        }, 20 * 6);
    }
}