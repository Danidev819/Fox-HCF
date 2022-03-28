package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FakeLogger extends Ability {

    private Main plugin;

    public FakeLogger() {
        super("FAKE_LOGGER");
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

            if (Main.get().getFakelogger().onCooldown(player)) {
                player.sendMessage(CC.translate("&7You are on &6&lFake Logger &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getFakelogger().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(player)){
                player.sendMessage(CC.translate("&7You are on &d&lPartner Item &7cooldown for &6" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true)));
                player.updateInventory();
                return;
            }

            PlayerUtil.decrement(player);

            Main.get().getFakelogger().applyCooldown(player, 60 * 1000);
            Main.get().getPartnerItem().applyCooldown(player,  10 * 1000);

            Villager villager = player.getWorld().spawn(
                    new Location(player.getWorld(),
                            player.getLocation().getX(),
                            player.getLocation().getY()
                                    + 5, player.getLocation().getZ()),
                    Villager.class);

            villager.setCustomName(ChatColor.YELLOW + player.getName());
            villager.setCustomNameVisible(true);

            plugin.getAbilityManager().cooldownExpired(player, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(player, this.getAbility());
        }
    }
}
