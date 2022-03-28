package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.GuavaCompat;
import me.danidev.core.managers.wrench.Wrench;
import me.danidev.core.utils.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import java.util.List;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;
import org.bukkit.inventory.ItemStack;
import me.danidev.core.managers.faction.FactionMember;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class WrenchListener implements Listener {

	public WrenchListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Optional<Wrench> crowbarOptional;
		if (event.getAction() == Action.LEFT_CLICK_BLOCK
				&& event.hasItem()
				&& (crowbarOptional = Wrench.fromStack(event.getItem())).isPresent()) {
			event.setCancelled(true);
			Player player = event.getPlayer();
			World world = player.getWorld();

			if (world.getEnvironment() != World.Environment.NORMAL) {
				player.sendMessage(ChatColor.RED + "Wrench may only be used in the overworld.");
				return;
			}

			Block block = event.getClickedBlock();
			Location blockLocation = block.getLocation();

			if (!FactionsCoreListener.build(player, blockLocation, ChatColor.YELLOW + "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.')) {
				return;
			}

			Wrench wrench = crowbarOptional.get();
			BlockState blockState = block.getState();

			if (blockState instanceof CreatureSpawner) {
				int remainingUses = wrench.getSpawnerUses();

				if (remainingUses <= 0) {
					player.sendMessage(ChatColor.RED + "This wrench has no more Spawner uses.");
					return;
				}

				wrench.setSpawnerUses(remainingUses - 1);
				player.setItemInHand(wrench.getItemIfPresent());

				CreatureSpawner spawner = (CreatureSpawner) blockState;

				block.setType(Material.AIR);
				blockState.update();
				world.dropItemNaturally(blockLocation, getSpawner(spawner));
			}
			else if (block.getType() == Material.ENDER_PORTAL_FRAME) {
				if (block.getType() != Material.ENDER_PORTAL_FRAME) return;

				int remainingUses = wrench.getEndFrameUses();

				if (remainingUses <= 0) {
					player.sendMessage(ChatColor.RED + "This wrench has no more End Portal Frame uses.");
					return;
				}

				boolean destroyed = false;
				int blockX = blockLocation.getBlockX();
				int blockY = blockLocation.getBlockY();
				int blockZ = blockLocation.getBlockZ();

				for (int searchRadius = 4, x = blockX - searchRadius; x <= blockX + searchRadius; ++x) {
					for (int z = blockZ - searchRadius; z <= blockZ + searchRadius; ++z) {
						Block next = world.getBlockAt(x, blockY, z);
						if (next.getType() == Material.ENDER_PORTAL) {
							next.setType(Material.AIR);
							next.getState().update();
							destroyed = true;
						}
					}
				}
				if (destroyed) {
					PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());
					player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Ender Portal is no longer active");

					if (playerFaction != null) {
						boolean informFaction = false;

						for (Claim claim : playerFaction.getClaims()) {
							if (!claim.contains(blockLocation)) continue;
							informFaction = true;
							break;
						}
						if (informFaction) {
							FactionMember factionMember = playerFaction.getMember(player);
							String astrix = factionMember.getRole().getAstrix();
							playerFaction.broadcast(astrix + ConfigurationService.TEAMMATE_COLOR
									+ " has used a Wrench de-activating one of the factions' end portals.", player.getUniqueId());
						}
					}
				}

				wrench.setEndFrameUses(remainingUses - 1);
				player.setItemInHand(wrench.getItemIfPresent());
				block.setType(Material.AIR);
				blockState.update();
				world.dropItemNaturally(blockLocation, new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
			}
			else {
				if (block.getType() != Material.DRAGON_EGG) return;

				int remainingUses = wrench.getEndDragonUses();

				if (remainingUses != 1) {
					player.sendMessage(ChatColor.RED + "This wrench has no more Dragon egg uses.");
					return;
				}
				wrench.setEndDragonUses(0);
				player.setItemInHand(wrench.getItemIfPresent());
				block.setType(Material.AIR);
				blockState.update();
				String[] lore = {
						ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " took from "
						+ ((Main.get().getFactionManager().getFactionAt(blockLocation) != null)
								? Main.get().getFactionManager().getFactionAt(blockLocation).getName()
								: "wilderness")
				};
				world.dropItemNaturally(blockLocation, new ItemBuilder(Material.DRAGON_EGG)
						.name(ChatColor.WHITE + "Dragon Egg")
						.lore(lore)
						.build());
			}
			if (event.getItem().getType() == Material.AIR) {
				player.playSound(blockLocation, Sound.ITEM_BREAK, 1.0f, 1.0f);
			}
			else {
				player.playSound(blockLocation, Sound.LEVEL_UP, 1.0f, 1.0f);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		ItemStack stack = event.getItemInHand();
		Player player = event.getPlayer();

		if (block.getState() instanceof CreatureSpawner && stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();

			if (meta.hasLore() && meta.hasDisplayName()) {
				CreatureSpawner spawner = (CreatureSpawner) block.getState();
				List<String> lore = meta.getLore();

				if (!lore.isEmpty()) {
					String spawnerName = ChatColor.stripColor(lore.get(0).toUpperCase());
					Optional<EntityType> entityTypeOptional = GuavaCompat.getIfPresent(EntityType.class, spawnerName);

					if (entityTypeOptional.isPresent()) {
						spawner.setSpawnedType(entityTypeOptional.get());
						spawner.update(true, true);
						player.sendMessage(ChatColor.YELLOW + "Placed a " + ChatColor.GREEN + spawnerName + " Spawner.");
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPrepareCrowbarCraft(PrepareItemCraftEvent event) {
		CraftingInventory inventory = event.getInventory();

		if (event.isRepair() && event.getRecipe().getResult().getType() == Wrench.WRENCH_TYPE) {
			int endFrameUses = 0;
			int spawnerUses = 0;
			int dragonUses = 0;
			boolean changed = false;

			ItemStack[] array = inventory.getMatrix();

			for (ItemStack ingredient : array) {
				Optional<Wrench> crowbarOptional = Wrench.fromStack(ingredient);

				if (crowbarOptional.isPresent()) {
					Wrench wrench = crowbarOptional.get();
					spawnerUses += wrench.getSpawnerUses();
					dragonUses += wrench.getEndDragonUses();
					endFrameUses += wrench.getEndFrameUses();
					changed = true;
				}
			}
			if (changed) {
				inventory.setResult(new Wrench(spawnerUses, endFrameUses, dragonUses).getItemIfPresent());
			}
		}
	}

	private ItemStack getSpawner(CreatureSpawner spawner) {
		return new ItemBuilder(Material.MOB_SPAWNER)
				.name("&aSpawner")
				.data(spawner.getData().getData())
				.lore(ChatColor.WHITE + WordUtils.capitalizeFully(spawner.getSpawnedType().name()))
				.build();
	}
}
