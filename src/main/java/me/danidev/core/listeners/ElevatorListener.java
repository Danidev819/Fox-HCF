package me.danidev.core.listeners;

import java.util.ArrayList;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener {

	public  ElevatorListener (Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, Main.get());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		try {
			Player p = e.getPlayer();
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign)e.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase(CC.translate(Main.get().getMainConfig().getString("ELEVATOR.FORMAT"))))
					if (sign.getLine(1).equalsIgnoreCase("Up")) {
						boolean tped = false;
						for (int i = 0; i <= 256; i++) {
							if (e.getClickedBlock().getY() < i &&
									approvedLocation(new Location(e.getClickedBlock().getWorld(), e.getClickedBlock().getLocation().getX(), i, e.getClickedBlock().getLocation().getZ())) &&
									approvedLocation(new Location(e.getClickedBlock().getWorld(), e.getClickedBlock().getLocation().getX(), (i + 1), e.getClickedBlock().getLocation().getZ()))) {
								e.getPlayer().teleport(new Location(e.getPlayer().getWorld(), e.getClickedBlock().getLocation().getX(), i, e.getClickedBlock().getLocation().getZ()));
								tped = true;
								break;
							}
						}
						if (!tped)
							p.sendMessage(ChatColor.RED + "No safe spot to tp was found!");
					} else if (sign.getLine(1).equalsIgnoreCase("Down")) {
						boolean tped = false;
						for (int i = 256; i >= 0; i--) {
							if (i < e.getClickedBlock().getY() &&
									approvedLocation(new Location(e.getClickedBlock().getWorld(), e.getClickedBlock().getLocation().getX(), i, e.getClickedBlock().getLocation().getZ())) &&
									approvedLocation(new Location(e.getClickedBlock().getWorld(), e.getClickedBlock().getLocation().getX(), (i - 1), e.getClickedBlock().getLocation().getZ()))) {
								e.getPlayer().teleport(new Location(e.getClickedBlock().getWorld(), e.getClickedBlock().getLocation().getX(), (i - 1), e.getClickedBlock().getLocation().getZ()));
								tped = true;
								break;
							}
						}
						if (!tped)
							p.sendMessage(ChatColor.RED + "No safe spot to tp was found!");
					} else {
						p.sendMessage(ChatColor.RED + "That elevator sign is invalid!");
					}
			}
		} catch (Exception exception) {}
	}

	public boolean approvedLocation(Location loc) {
		List<Material> approvedMaterials = new ArrayList<>();
		approvedMaterials.add(Material.AIR);
		approvedMaterials.add(Material.SIGN);
		approvedMaterials.add(Material.SIGN_POST);
		approvedMaterials.add(Material.WALL_SIGN);
		for (Material mat : approvedMaterials) {
			if (loc.getWorld().getBlockAt(loc).getType() == mat)
				return true;
		}
		return false;
	}

	@EventHandler
	public void onChange(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[Elevator]"))
			if (e.getLine(1).equalsIgnoreCase("Up") || e.getLine(1).equalsIgnoreCase("Down")) {
				e.setLine(0, CC.translate(Main.get().getMainConfig().getString("ELEVATOR.FORMAT")));
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + "Invalid Elevator sign. Please use Up or Down.");
			}
	}
}
