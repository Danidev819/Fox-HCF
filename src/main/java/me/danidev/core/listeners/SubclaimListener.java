package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import java.util.Set;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.block.Chest;
import java.util.HashSet;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.BlockState;
import org.bukkit.GameMode;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.stream.Collectors;

import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import org.bukkit.event.block.SignChangeEvent;
import java.util.Collection;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import java.util.regex.Pattern;
import java.util.List;
import org.bukkit.event.Listener;

public class SubclaimListener implements Listener {

	private final String SUBCLAIM_CONVERSION_PREFIX;
	private final List<String> SUBCLAIM_ALIASES;
	private final Pattern SQUARE_PATTERN_REPLACER;
	private final BlockFace[] SIGN_FACES;

	public SubclaimListener(Main plugin) {
		SUBCLAIM_CONVERSION_PREFIX = String.valueOf(ChatColor.DARK_RED.toString()) + ChatColor.BOLD + "[Subclaim]";
		SUBCLAIM_ALIASES = Arrays.asList("SUBCLAIM", "PRIVATE", "[SUBCLAIM]", "[PRIVATE]");
		SQUARE_PATTERN_REPLACER = Pattern.compile("[\\[]]");
		SIGN_FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP };

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private boolean isSubclaimable(Block block) {
		Material type = block.getType();
		return type == Material.FENCE_GATE || type == Material.TRAP_DOOR || block.getState() instanceof InventoryHolder;
	}

	private boolean isSubclaimed(Block block) {
		if (this.isSubclaimable(block)) {
			Collection<Sign> attachedSigns = this.getAttachedSigns(block);
			for (Sign attachedSign : attachedSigns) {
				if (attachedSign.getLine(0).equals(SUBCLAIM_CONVERSION_PREFIX)) {
					return false;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {
		String[] lines = event.getLines();

		if (!SUBCLAIM_ALIASES.contains(SQUARE_PATTERN_REPLACER.matcher(lines[0].toUpperCase()).replaceAll(""))) {
			return;
		}

		Block block = event.getBlock();
		MaterialData materialData = block.getState().getData();

		if (materialData instanceof org.bukkit.material.Sign) {
			org.bukkit.material.Sign sign = (org.bukkit.material.Sign) materialData;
			Block attachedBlock = block.getRelative(sign.getAttachedFace());

			if (this.isSubclaimable(attachedBlock)) {
				Player player = event.getPlayer();
				PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

				if (playerFaction == null) return;

				Faction factionAt = Main.get().getFactionManager().getFactionAt(block.getLocation());

				if (playerFaction == factionAt) {
					if (this.isSubclaimed(attachedBlock)) {
						player.sendMessage(ChatColor.RED + "There is already a subclaim sign on this " + attachedBlock.getType().name() + '.');
						return;
					}

					List<String> memberList = new ArrayList<>(3);

					for (int i = 1; i < lines.length; ++i) {
						String line = lines[i];
						if (StringUtils.isNotBlank(line)) memberList.add(line);
					}

					if (memberList.isEmpty()) {
						player.sendMessage(ChatColor.RED + "Subclaim signs need to have at least 1 player name inserted.");
						return;
					}

					boolean leaderChest = lines[1].equals(Role.LEADER.getAstrix()) || lines[1].equalsIgnoreCase("LEADER");

					if (leaderChest) {
						if (playerFaction.getMember(player).getRole() != Role.LEADER) {
							player.sendMessage(ChatColor.RED + "Only faction leaders can create leader subclaimed objects.");
							return;
						}

						event.setLine(2, null);
						event.setLine(3, null);
					}

					event.setLine(0, SUBCLAIM_CONVERSION_PREFIX);
					List<String> actualMembers = memberList.stream()
							.filter(member -> playerFaction.getMember(member) != null)
							.collect(Collectors.toList());
					playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOR + player.getName() + ChatColor.YELLOW
									+ " has created a subclaim on block others " + ChatColor.AQUA
									+ attachedBlock.getType().toString() + ChatColor.YELLOW + " at " + ChatColor.WHITE
									+ '(' + attachedBlock.getX() + ", " + attachedBlock.getZ() + ')' + ChatColor.YELLOW
									+ " for "
									+ (leaderChest ? "leaders"
											: (actualMembers.isEmpty() ? "captains"
													: ("members " + ChatColor.RED + '['
															+ StringUtils.join(actualMembers, ", ")
															+ ']'))));
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Main.get().getEotwHandler().isEndOfTheWorld()) return;

		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("fhcf.protection.bypass")) return;

		Block block = event.getBlock();
		BlockState state = block.getState();
		Block subclaimObjectBlock = null;

		if (!(state instanceof Sign)) {
			subclaimObjectBlock = block;
		}
		else {
			Sign sign = (Sign) state;
			MaterialData signData = sign.getData();

			if (signData instanceof org.bukkit.material.Sign) {
				org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) signData;
				subclaimObjectBlock = block.getRelative(materialSign.getAttachedFace());
			}
		}
		if (subclaimObjectBlock != null && !this.checkSubclaimIntegrity(player, subclaimObjectBlock)) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot break this subclaimed " + subclaimObjectBlock.getType().toString() + '.');
		}
	}

	private String getShortenedName(String originalName) {
		if (originalName.length() >= 16) {
			originalName = originalName.substring(0, 16);
		}
		return originalName;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("fhcf.protection.bypass")) return;

		if (Main.get().getEotwHandler().isEndOfTheWorld()) return;

		Block block = event.getClickedBlock();

		if (!this.isSubclaimable(block)) return;

		if (!this.checkSubclaimIntegrity(player, block)) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to this subclaimed " + block.getType().toString() + '.');
		}
	}

	private boolean checkSubclaimIntegrity(Player player, Block subclaimObject) {
		if (!this.isSubclaimable(subclaimObject)) return true;

		PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

		if (playerFaction == null || playerFaction.isRaidable()) return true;

		Role role = playerFaction.getMember(player).getRole();

		if (role == Role.LEADER) return true;

		if (playerFaction != Main.get().getFactionManager().getFactionAt(subclaimObject)) return true;

		Collection<Sign> attachedSigns = this.getAttachedSigns(subclaimObject);

		if (attachedSigns.isEmpty()) return true;

		boolean hasLooped = false;
		String search = this.getShortenedName(player.getName());

		for (Sign attachedSign : attachedSigns) {
			String[] lines = attachedSign.getLines();

			if (!lines[0].equals(SUBCLAIM_CONVERSION_PREFIX)) continue;

			hasLooped = true;

			if (Role.LEADER.getAstrix().equals(lines[1])) continue;
			if (Role.COLEADER.getAstrix().equals(lines[1])) continue;
			if (Role.CAPTAIN.getAstrix().equals(lines[1])) continue;
			if (Role.LEADER.getName().equals(lines[1])) continue;
			if (Role.COLEADER.getName().equals(lines[1])) continue;
			if (Role.CAPTAIN.getName().equals(lines[1])) continue;
			if (role == Role.CAPTAIN) return true;
			if (role == Role.COLEADER) return true;
			for (int i = 1; i < lines.length; ++i) {
				if (lines[i].toLowerCase().contains(search.toLowerCase())) return true;
			}
		}
		return !hasLooped;
	}

	private Collection<Sign> getAttachedSigns(Block block) {
		Set<Sign> results = new HashSet<>();

		this.getSignsAround(block, results);

		BlockState state = block.getState();

		if (state instanceof Chest) {
			Inventory chestInventory = ((Chest) state).getInventory();

			if (chestInventory instanceof DoubleChestInventory) {
				DoubleChest doubleChest = ((DoubleChestInventory) chestInventory).getHolder();
				Block left = ((Chest) doubleChest.getLeftSide()).getBlock();
				Block right = ((Chest) doubleChest.getRightSide()).getBlock();
				this.getSignsAround(left.equals(block) ? right : left, results);
			}
		}
		return results;
	}

	private Set<Sign> getSignsAround(Block block, Set<Sign> results) {
		BlockFace[] sign_FACES;

		for (int length = (sign_FACES = SIGN_FACES).length, i = 0; i < length; ++i) {
			BlockFace face = sign_FACES[i];
			Block relative = block.getRelative(face);
			BlockState relativeState = relative.getState();

			if (relativeState instanceof Sign) {
				org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) relativeState.getData();
				if (relative.getRelative(materialSign.getAttachedFace()).equals(block)) {
					results.add((Sign) relative.getState());
				}
			}
		}
		return results;
	}
}
