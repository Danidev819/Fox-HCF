package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.kit.event.KitApplyEvent;
import me.danidev.core.managers.timer.event.TimerStartEvent;
import me.danidev.core.managers.timer.type.ProtectionTimer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;

public class KitMapListener implements Listener {

	public KitMapListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onTimer(TimerStartEvent event) {
		if (event.getTimer() instanceof ProtectionTimer) {
			Main.get().getTimerManager().protectionTimer.clearCooldown(event.getUserUUID().get());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (Main.get().getTimerManager().protectionTimer.getRemaining(event.getPlayer()) >= 0L) {
			Main.get().getTimerManager().protectionTimer.clearCooldown(event.getPlayer());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onKitApplyMonitor(KitApplyEvent event) {
		Player player = event.getPlayer();
		player.getInventory().clear();

		for (PotionEffect potionEffect : player.getActivePotionEffects()) {
			player.removePotionEffect(potionEffect.getType());
		}

		player.getInventory().setArmorContents(null);
	}
}
