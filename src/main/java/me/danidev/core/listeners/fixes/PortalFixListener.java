package me.danidev.core.listeners.fixes;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class PortalFixListener implements Listener {

    public PortalFixListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onClickPortal(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getType() == Material.PORTAL) {
            Player player = event.getPlayer();

            player.sendMessage(CC.translate("&eYou activated your stuck timer by right clicking the &5Nether Portal&e."));
            player.performCommand("f stuck");
        }
    }
}
