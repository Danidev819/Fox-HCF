package me.danidev.core.listeners;

import java.util.concurrent.TimeUnit;

import me.danidev.core.managers.LivesManager;
import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import me.danidev.core.managers.deathban.Deathban;
import me.danidev.core.managers.deathban.DeathbanManager;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.TaskUtils;

public class DeathbanListener implements Listener {

    public DeathbanListener() {
        Bukkit.getPluginManager().registerEvents(this, Main.get());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        DeathbanManager deathbanManager = Main.get().getDeathbanManager();
        Player player = event.getEntity();

        // Is in deathban arena
        if (deathbanManager.isDeathbanned(player.getUniqueId())) {
            Player killer = player.getKiller();

            if (killer == null) {
                return;
            }

            Deathban killerDeathban = deathbanManager.get(killer.getUniqueId());

            if (killerDeathban == null || !killerDeathban.isActive()) {
                return;
            }

            long timeleft = killerDeathban.getExpiryMillis();

            killerDeathban.setExpiryMillis(timeleft - TimeUnit.MINUTES.toMillis(5));

            killer.sendMessage(CC.translate("&aYour deathban time has been reduced &l5m &abecause you killed &l" + player.getName() + "&a."));
            return;
        }

        if (player.isOp() || player.hasPermission("fhcf.deathban.bypass")) {
            return;
        }

        String reason = event.getDeathMessage();
        Deathban deathban = new Deathban(player.getUniqueId(), reason, player.getLocation().clone());

        deathbanManager.getDeathbansMap().put(player.getUniqueId(), deathban);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        DeathbanManager deathbanManager = Main.get().getDeathbanManager();
        Player player = event.getPlayer();

        if (!deathbanManager.isDeathbanned(player.getUniqueId())) {
            return;
        }

        if (player.isOp() || player.hasPermission("fhcf.deathban.bypass")) {
            return;
        }

        TaskUtils.runLater(() -> deathbanManager.sendToDeathbanArena(player), 2);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        DeathbanManager deathbanManager = Main.get().getDeathbanManager();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block.getType().name().contains("SIGN")) {
            Sign sign = (Sign) block.getState();

            if (!this.isRespawn(sign)) {
                return;
            }

            Deathban deathban = deathbanManager.get(player.getUniqueId());

            if (deathban == null || !deathban.isActive()) {
                deathban.revive();
                deathbanManager.getDeathbansMap().remove(player.getUniqueId());
                return;
            } else {
                player.sendMessage(CC.translate("&cYou don't have deathban active"));
            }

            LivesManager livesManager = Main.get().getLivesManager();
            int lives = livesManager.getLives(player.getUniqueId());

            if (lives == 0) {
                player.sendMessage(CC.translate("&cYou don't have lives to use!"));
                return;
            }

            player.sendMessage(CC.translate("&aYou have used a live to skip your deathban!"));

            livesManager.takeLives(player.getUniqueId(), 1);

            deathban.revive();
            deathbanManager.getDeathbansMap().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }
        if (event.getLine(0).equals("[Deathban]")) {
                event.setLine(0, "");
                event.setLine(1, CC.translate("&6&lHave a live"));
                event.setLine(2, CC.translate("&7Click here"));
                event.setLine(3, "");
                return;
            }
        }

    private boolean isRespawn(Sign sign) {
        String line1 = sign.getLine(1);
        String line2 = sign.getLine(2);

        return line1.equals(CC.translate("&6&lHave a live")) && line2.equals(CC.translate("&7Click here"));
    }
}