package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import me.danidev.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class AntiTrapper extends Ability {

    private final Main plugin = Main.get();

    public static Map<String, Long> cooldowndam;
    public static Map<String, Long> cooldownvic;
    public int count;

    public AntiTrapper() {
        super("ANTI_TRAPPER");
        AntiTrapper.cooldownvic = new HashMap<String, Long>();
        this.count = 0;
    }

    static {
        AntiTrapper.cooldownvic = new HashMap<String, Long>();
    }

    public static boolean isOnCooldownVic(Player player) {
        return AntiTrapper.cooldownvic.containsKey(player.getName())
                && AntiTrapper.cooldownvic.get(player.getName()) > System.currentTimeMillis();
    }

    public static Boolean hasCooldownVic(final Player player) {
        if (AntiTrapper.cooldownvic.containsKey(player.getName())
                && AntiTrapper.cooldownvic.get(player.getName()) > System.currentTimeMillis()) {
            return true;
        }
        return true;
    }

    public static String getCooldownVic(final Player player) {
        final long millisLeft = AntiTrapper.cooldownvic.get(player.getName()) - System.currentTimeMillis();
        return Utils.formatLongMin(millisLeft);
    }
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if (!isAbility(damager.getItemInHand())) {
                return;
            }
            if (isAbility(damager.getItemInHand())) {
                if(Main.get().getFactionManager().getPlayerFaction(damager.getUniqueId()) != null) {
                    PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(damager.getUniqueId());
                    PlayerFaction victimFaction = Main.get().getFactionManager().getPlayerFaction(victim.getUniqueId());
                    if(playerFaction.equals(victimFaction)) {
                        damager.sendMessage(CC.translate("&eYou can't use this item with a member of your Faction"));
                        return;
                    }
                }
                if (Main.get().getAntiTrapper().onCooldown(damager)) {
                    damager.sendMessage(CC.translate("&6You are on &6&lAntiTrapper &6cooldown for &7" + DurationFormatter.getRemaining(Main.get().getAntiTrapper().getRemainingMilis(damager), true, true)));
                    damager.updateInventory();
                    return;
                }

                if (Main.get().getPartnerItem().onCooldown(damager)) {
                    damager.sendMessage(CC.translate("&6You are on &d&lPartner Item &6cooldown for &7" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(damager), true, true)));
                    damager.updateInventory();
                    return;
                }

                Faction factionAt = Main.get().getFactionManager().getFactionAt(damager.getLocation());
                Faction factionAt2 = Main.get().getFactionManager().getFactionAt(victim.getLocation());
                if (factionAt.isSafezone()) {
                    damager.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                    damager.updateInventory();
                    return;
                }
                if (factionAt2.isSafezone()) {
                    damager.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                    damager.updateInventory();
                    return;
                }
                count = count + 1;
                if (count >= 3) {
                    count = 0;
                    Main.get().getAntiTrapper().applyCooldown(damager, 60 * 1000);
                    Main.get().getPartnerItem().applyCooldown(damager,  10 * 1000);
                    AntiTrapper.cooldownvic.put(victim.getName(), System.currentTimeMillis() + (15 * 1000));
                    this.plugin.getAbilityManager().playerMessage(damager, this.getAbility().replace("%TARGET%", victim.getName()));
                    this.plugin.getAbilityManager().targetMessage(victim, damager, this.getAbility());
                    PlayerUtil.decrement(damager);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (!isAbility(player.getItemInHand())) {
                return;
            }
            if (isAbility(player.getItemInHand())) {
                if (Main.get().getAntiTrapper().onCooldown(player)) {
                    player.sendMessage(CC.translate("&6You are on cooldown for &7%cooldown%")
                            .replace("%cooldown%", DurationFormatter.getRemaining(Main.get().getAntiTrapper().getRemainingMilis(player), true, true)));
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (AntiTrapper.isOnCooldownVic(player)) {
            long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
            event.setCancelled(true);
            player.sendMessage(CC.translate("&6You can't place blocks for another &7%cooldown% &6seconds").replace("%cooldown%", Utils.formatLongMin(millisLeft)));
            return;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (AntiTrapper.isOnCooldownVic(player)) {
            long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
            event.setCancelled(true);
            player.sendMessage(CC.translate("&6You can't place blocks for another &7%cooldown% &6seconds").replace("%cooldown%", Utils.formatLongMin(millisLeft)));
            return;
        }
    }

    @EventHandler
    public void onFenceInteract(PlayerInteractEvent event) {
        Player player = (Player) event.getPlayer();
        Block block = (Block) event.getClickedBlock();
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (block.getType().equals(Material.FENCE_GATE) || block.getType().equals(Material.CHEST)) {
                if (AntiTrapper.isOnCooldownVic(player)) {
                    long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
                    event.setCancelled(true);
                    player.sendMessage(CC
                            .translate("&6You can't interact with blocks for another &7%cooldown% &6seconds").replace("%cooldown%", Utils.formatLongMin(millisLeft)));
                    return;
                }
            }
        }
    }
}
