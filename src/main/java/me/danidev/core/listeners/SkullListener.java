package me.danidev.core.listeners;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;

import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.SkullType;
import org.bukkit.ChatColor;
import org.bukkit.block.Skull;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class SkullListener implements Listener {

	public SkullListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			BlockState blockState = event.getClickedBlock().getState();

			if (blockState instanceof Skull) {
				Skull skull;
				player.sendMessage(ChatColor.YELLOW + 
						"This head belongs to " + ChatColor.WHITE + (((skull = (Skull) blockState).getSkullType() == SkullType.PLAYER &&
						skull.hasOwner()) ? skull.getOwner() : ("a " + WordUtils.capitalizeFully(skull.getSkullType().name()) + " skull"))
						+ ChatColor.YELLOW + '.');
			}
		}
	}
}
