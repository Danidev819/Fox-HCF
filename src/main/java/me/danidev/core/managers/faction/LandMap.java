package me.danidev.core.managers.faction;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.visualise.VisualBlockData;
import me.danidev.core.utils.visualise.VisualType;

import java.util.Objects;
import java.util.LinkedHashMap;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Location;
import java.util.ArrayList;
import org.bukkit.ChatColor;

import java.util.LinkedHashSet;

import org.bukkit.entity.Player;

public class LandMap {

	private static int FACTION_MAP_RADIUS_BLOCKS = 22;

	public static boolean updateMap(Player player, Main plugin, VisualType visualType, boolean inform) {
		Location location = player.getLocation();
		World world = player.getWorld();
		int locationX = location.getBlockX();
		int locationZ = location.getBlockZ();
		int minimumX = locationX - 22;
		int minimumZ = locationZ - 22;
		int maximumX = locationX + 22;
		int maximumZ = locationZ + 22;
		LinkedHashSet<Claim> board = new LinkedHashSet<Claim>();
		if (visualType != VisualType.CLAIM_MAP) {
			player.sendMessage(ChatColor.RED + "Not supported: " + visualType.name().toLowerCase() + '.');
			return false;
		}
		for (int x = minimumX; x <= maximumX; ++x) {
			for (int z = minimumZ; z <= maximumZ; ++z) {
				Claim claim = plugin.getFactionManager().getClaimAt(world, x, z);
				if (claim != null && claim.getFaction() != null) {
					board.add(claim);
				}
			}
		}
		if (board.isEmpty()) {
			player.sendMessage(ChatColor.RED + "No claims are in your visual range to display.");
			return false;
		}
		for (Claim claim2 : board) {
			int maxHeight = Math.min(world.getMaxHeight(), 256);
			Location[] corners = claim2.getCornerLocations();
			ArrayList<Location> shown = new ArrayList<Location>(maxHeight * corners.length);
			Location[] array;
			for (int length = (array = corners).length, i = 0; i < length; ++i) {
				Location corner = array[i];
				for (int y = 0; y < maxHeight; ++y) {
					shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
				}
			}
			LinkedHashMap<Location, VisualBlockData> dataMap = plugin.getVisualiseHandler().generate(player,
					shown, visualType, true);
			if (dataMap.isEmpty()) {
				continue;
			}
			String materialName = ChatColor.RED + "Error!";
			for (VisualBlockData visualBlockData : dataMap.values()) {
				if (visualBlockData.getItemType() == Material.STAINED_GLASS) {
					continue;
				}
				materialName = Main.get().getItemDb().getName(new ItemStack(visualBlockData.getItemType()));
				break;
			}
			if (!inform) {
				continue;
			}
			player.sendMessage(String.valueOf(claim2.getFaction().getDisplayName((CommandSender) player))
					+ ChatColor.YELLOW + " owns claim displayed by the " + ChatColor.AQUA + materialName);
		}
		return true;
	}

	public static Location getNearestSafePosition(Player player, Location origin, int searchRadius) {
		FactionManager factionManager = Main.get().getFactionManager();
		PlayerFaction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
		for (int x = 16; x < searchRadius; ++x) {
			for (int z = 16; z < searchRadius; ++z) {
				Location atPos = origin.clone().add((double) x, 0.0, (double) z);
				Faction factionAtPos = factionManager.getFactionAt(atPos);
				if (Objects.equals(factionAtPos, playerFaction) || !(factionAtPos instanceof PlayerFaction)) {
					return BukkitUtils.getHighestLocation(atPos, atPos);
				}
				Location atNeg = origin.clone().add((double) x, 0.0, (double) z);
				Faction factionAtNeg = factionManager.getFactionAt(atNeg);
				if (Objects.equals(factionAtNeg, playerFaction) || !(factionAtNeg instanceof PlayerFaction)) {
					return BukkitUtils.getHighestLocation(atNeg, atNeg);
				}
			}
		}
		return null;
	}
}
