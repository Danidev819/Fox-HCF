package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColouredSignListener implements Listener {

	public ColouredSignListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onSignCreate(SignChangeEvent event) {
		Player player = event.getPlayer();

		if (player != null && player.hasPermission("fhcf.sign.colour")) {
			String[] lines = event.getLines();

			for (int i = 0; i < lines.length; ++i) {
				if (event.getLine(i).contains(CC.translate("Sell"))
						|| event.getLine(i).contains(CC.translate("Buy"))
						|| event.getLine(i).contains(CC.translate("Kit"))) {
					if (player.hasPermission("fhcf.sign.admin")) {
						event.setLine(i, CC.translate(lines[i]));
					}
					else {
						player.sendMessage(CC.translate("&cYou have used a sign that you're not allowed."));
						event.setCancelled(true);
					}
				}
				event.setLine(i, CC.translate(lines[i]));
			}
		}
	}
}
