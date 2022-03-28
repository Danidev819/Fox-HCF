package me.danidev.core.managers.abilities.impl;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Switcher extends Ability {

    private final Main plugin = Main.get();

    public Switcher() {
        super("SWITCHER");
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (isAbility(shooter.getItemInHand())) {
                event.getEntity().setMetadata(this.getAbility(), new FixedMetadataValue(this.plugin, true));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isAbility(event.getItem())) return;

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player shooter = event.getPlayer();

            Faction factionAt = Main.get().getFactionManager().getFactionAt(shooter.getLocation());

            if(factionAt.isSafezone()){
                shooter.sendMessage(CC.translate("&cYou can't use abilitys in safezones."));
                event.setCancelled(true);
                return;
            }

            if (Main.get().getSwitcher().onCooldown(shooter)) {
                shooter.sendMessage(CC.translate("&bYou are on &6&lSwitcher &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getSwitcher().getRemainingMilis(shooter), true, true)));
                shooter.updateInventory();
                event.setCancelled(true);
                return;
            }

            if(Main.get().getPartnerItem().onCooldown(shooter)){
                shooter.sendMessage(CC.translate("&bYou are on &d&lPartner Item &bcooldown for &3" + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(shooter), true, true)));
                shooter.updateInventory();
                event.setCancelled(true);
                return;
            }

            Main.get().getSwitcher().applyCooldown(shooter, 8 * 1000);
            Main.get().getPartnerItem().applyCooldown(shooter,  10 * 1000);

            plugin.getAbilityManager().cooldownExpired(shooter, this.getName(), this.getAbility());
            plugin.getAbilityManager().playerMessage(shooter, this.getAbility());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile instanceof Egg && projectile.hasMetadata(this.getAbility())) {
                Player player = (Player) event.getEntity();
                Player shooter = (Player) projectile.getShooter();

                Location playerLocation = player.getLocation().clone();
                Location shooterLocation = shooter.getLocation().clone();
                Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());
                Faction factionAt1 = Main.get().getFactionManager().getFactionAt(player.getLocation());

                if(factionAt.isSafezone()){
                    shooter.sendMessage(CC.translate("&cYou can´t use this ability against players who are in safezone"));
                    event.setCancelled(true);
                    return;
                }

                if(factionAt1.isSafezone()){
                    shooter.sendMessage(CC.translate("&cYou can´t use this ability in safezone."));
                    event.setCancelled(true);
                    return;
                }

                player.teleport(shooterLocation);
                shooter.teleport(playerLocation);

                this.plugin.getAbilityManager().targetMessage(player, shooter, this.getAbility());
            }
            else if (projectile instanceof Snowball && projectile.hasMetadata(this.getAbility())) {
                Player player = (Player) event.getEntity();
                Player shooter = (Player) projectile.getShooter();

                Location playerLocation = player.getLocation().clone();
                Location shooterLocation = shooter.getLocation().clone();
                Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());
                Faction factionAt1 = Main.get().getFactionManager().getFactionAt(player.getLocation());

                if(factionAt.isSafezone()){
                    shooter.sendMessage(CC.translate("&cYou can´t use this ability against players who are in safezone"));
                    event.setCancelled(true);
                    return;
                }

                if(factionAt1.isSafezone()){
                    shooter.sendMessage(CC.translate("&cYou can´t use this ability in safezone."));
                }
                player.teleport(shooterLocation);
                shooter.teleport(playerLocation);

                this.plugin.getAbilityManager().targetMessage(player, shooter, this.getAbility());
            }
        }
    }
}