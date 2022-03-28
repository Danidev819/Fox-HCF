package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.timer.type.sotw.SOTWCommand;
import me.danidev.core.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.entity.Player;

import org.bukkit.event.Listener;

public class SOTWListener implements Listener {

	private final Main plugin;

	public SOTWListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			final Player player = (Player)e.getEntity();
			final Player oponent = (Player)e.getDamager();
			if (this.plugin.getSotwTimer().getSotwRunnable() != null && SOTWCommand.enabled.contains(oponent.getUniqueId()) && !SOTWCommand.enabled.contains(player.getUniqueId())) {
				oponent.sendMessage(CC.translate(ChatColor.GRAY + "&m----------------------------------"));
				oponent.sendMessage(CC.translate("&eYou are not permitted to hit this player, they do not have their &a&lSOTW &epaused."));
				oponent.sendMessage(CC.translate(ChatColor.GRAY + "&m----------------------------------"));
				e.setCancelled(true);
			}
			else if (this.plugin.getSotwTimer().getSotwRunnable() != null && !SOTWCommand.enabled.contains(oponent.getUniqueId()) && SOTWCommand.enabled.contains(player.getUniqueId())) {
				oponent.sendMessage(CC.translate(ChatColor.GRAY + "&m----------------------------------"));
				oponent.sendMessage(CC.translate("&eYou are not permitted to hit &a&l" + player.getName() + "&e."));
				oponent.sendMessage(CC.translate(ChatColor.GRAY + "&m----------------------------------"));
				e.setCancelled(true);
			}
			else if (this.plugin.getSotwTimer().getSotwRunnable() != null  && !SOTWCommand.enabled.contains(oponent.getUniqueId()) && !SOTWCommand.enabled.contains(player.getUniqueId())) {
				oponent.sendMessage(CC.translate(CC.translate(ChatColor.GRAY + "&m----------------------------------")));
				oponent.sendMessage(CC.translate("&eYou cannot hit players whilst sotw is active if you would like to execute &a&l/sotw enable&e."));
				oponent.sendMessage(CC.translate(ChatColor.GRAY + "&m----------------------------------"));
				e.setCancelled(true);
			}

		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && this.plugin.getSotwTimer().getSotwRunnable() != null) {
			final Player player = (Player)event.getEntity();
			if (SOTWCommand.enabled.contains(player.getUniqueId())) {
				event.setCancelled(false);
				return;
			}
			if (event.getCause() != EntityDamageEvent.DamageCause.SUICIDE && this.plugin.getSotwTimer().getSotwRunnable() != null) {
				event.setCancelled(true);
			}
		}
	}
}

