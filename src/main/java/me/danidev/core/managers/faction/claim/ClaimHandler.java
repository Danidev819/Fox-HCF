package me.danidev.core.managers.faction.claim;

import java.util.Set;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.faction.type.RoadFaction;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.cuboid.Cuboid;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.visualise.VisualType;
import me.danidev.core.managers.faction.FactionManager;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import org.bukkit.command.CommandSender;
import com.google.common.base.Preconditions;

import org.bukkit.World;
import org.bukkit.Location;

import org.bukkit.entity.Player;
import java.util.concurrent.TimeUnit;

import me.danidev.core.managers.faction.type.WildernessFaction;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.concurrent.ConcurrentMap;
import org.bukkit.inventory.ItemStack;

public class ClaimHandler {

	public static final int MIN_CLAIM_HEIGHT = 0;
	public static final int MAX_CLAIM_HEIGHT = 256;
	public static final ItemStack CLAIM_WAND;
	public static final int MIN_CLAIM_RADIUS = 5;
	public static final int MAX_CHUNKS_PER_LIMIT = 16;
	private static final int NEXT_PRICE_MULTIPLIER_AREA = 250;
	private static final int NEXT_PRICE_MULTIPLIER_CLAIM = 500;
	private static final double CLAIM_SELL_MULTIPLIER = 0.8;
	public static final double CLAIM_PRICE_PER_BLOCK = 0.25;
	public static Object NEARBY_CLAIM_RADIUS;
	public final ConcurrentMap<Object, Object> claimSelectionMap;
	private final Main plugin;

	static {
		CLAIM_WAND = new ItemBuilder(Material.DIAMOND_HOE)
				.name(ChatColor.LIGHT_PURPLE + "Claim Wand")
				.lore(ChatColor.YELLOW + "Left or Right Click " + ChatColor.RED + "a Block" + ChatColor.YELLOW
						+ " to:",
						ChatColor.GRAY + "Set the first and second position of ",
						ChatColor.GRAY + "your Claim selection.", "",
						ChatColor.YELLOW + "Right Click " + ChatColor.RED + "the Air" + ChatColor.YELLOW + " to:",
						ChatColor.GRAY + "Clear your current Claim selection.", "",
						ChatColor.YELLOW + "Shift " + ChatColor.YELLOW + "Left Click " + ChatColor.RED
								+ "the Air or a Block" + ChatColor.YELLOW + " to:",
						ChatColor.GRAY + "Purchase your current Claim selection.")
				.build();
	}

	public ClaimHandler(final Main plugin) {
		this.plugin = plugin;
		this.claimSelectionMap = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build().asMap();
	}

	public int calculatePrice(final Cuboid claim, int currentClaims, final boolean selling) {
		if (currentClaims == -1 || !claim.hasBothPositionsSet()) {
			return 0;
		}
		int multiplier = 1;
		int remaining = claim.getArea();
		double price = 0.0;
		while (remaining > 0) {
			if (--remaining % 250 == 0) {
				++multiplier;
			}
			price += 0.25 * multiplier;
		}
		if (currentClaims != 0) {
			currentClaims = Math.max(currentClaims + (selling ? -1 : 0), 0);
			price += currentClaims * 500;
		}
		if (selling) {
			price *= 0.8;
		}
		return (int) price;
	}

	public boolean clearClaimSelection(final Player player) {
		final ClaimSelection claimSelection = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap
				.remove(player.getUniqueId());
		if (claimSelection != null) {
			this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CREATE_CLAIM_SELECTION, null);
			return true;
		}
		return false;
	}

	public boolean canSubclaimHere(final Player player, final Location location) {
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null) {
			player.sendMessage(ChatColor.RED + "You must be in a faction to subclaim land.");
			return false;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
			return false;
		}
		if (!this.plugin.getFactionManager().getFactionAt(location).equals(playerFaction)) {
			player.sendMessage(ChatColor.RED + "This location is not part of your factions' territory.");
			return false;
		}
		return true;
	}

	public boolean canClaimHere(final Player player, final Location location) {
		final World world = location.getWorld();
		if (world.getEnvironment() != World.Environment.NORMAL) {
			player.sendMessage(ChatColor.RED + "You can only claim land in the Overworld.");
			return false;
		}
		if (!(this.plugin.getFactionManager().getFactionAt(location) instanceof WildernessFaction)) {
			player.sendMessage(ChatColor.RED + "You can only claim land in the "
					+ ConfigurationService.WILDERNESS_COLOR + "Wilderness" + ChatColor.RED
					+ ". Make sure you are past " + ConfigurationService.WARZONE_RADIUS + " blocks from spawn..");
			return false;
		}
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null) {
			player.sendMessage(ChatColor.RED + "You must be in a faction to claim land.");
			return false;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
			return false;
		}
		if (playerFaction.getClaims().size() >= 8) {
			player.sendMessage(ChatColor.RED + "Your faction has maximum claims - " + 8);
			return false;
		}
		final int locX = location.getBlockX();
		final int locZ = location.getBlockZ();
		final FactionManager factionManager = this.plugin.getFactionManager();
		for (int x = locX - 5; x < locX + 5; ++x) {
			for (int z = locZ - 5; z < locZ + 5; ++z) {
				final Faction factionAtNew = factionManager.getFactionAt(world, x, z);
				final boolean b = factionAtNew instanceof RoadFaction;
				if (!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
					player.sendMessage(ChatColor.RED + "This position contains enemy claims within a " + 5
							+ " block buffer radius.");
					return false;
				}
			}
		}
		return true;
	}

	public boolean tryPurchasing(final Player player, final Claim claim) {
		Preconditions.checkNotNull((Object) claim, (Object) "Claim is null");
		final World world = claim.getWorld();
		if (world.getEnvironment() != World.Environment.NORMAL) {
			player.sendMessage(ChatColor.RED + "You can only claim land in the Overworld.");
			return false;
		}
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null) {
			player.sendMessage(ChatColor.RED + "You must be in a faction to claim land.");
			return false;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
			return false;
		}
		if (playerFaction.getClaims().size() >= 8) {
			player.sendMessage(ChatColor.RED + "Your faction has maximum claims - " + 8);
			return false;
		}
		final int factionBalance = playerFaction.getBalance();
		final int claimPrice = this.calculatePrice((Cuboid) claim, playerFaction.getClaims().size(), false);
		if (claimPrice > factionBalance) {
			player.sendMessage(ChatColor.RED + "Your faction bank only has " + '$' + factionBalance
					+ ", the price of this claim is " + '$' + claimPrice + '.');
			return false;
		}
		if (claim.getChunks().size() > 16) {
			player.sendMessage(ChatColor.RED + "Claims cannot exceed " + 16 + " chunks.");
			return false;
		}
		if (claim.getWidth() < 5 || claim.getLength() < 5) {
			player.sendMessage(ChatColor.RED + "Claims must be at least " + 5 + 'x' + 5 + " blocks.");
			return false;
		}
		final int minimumX = claim.getMinimumX();
		final int maximumX = claim.getMaximumX();
		final int minimumZ = claim.getMinimumZ();
		final int maximumZ = claim.getMaximumZ();
		final FactionManager factionManager = this.plugin.getFactionManager();
		for (int x = minimumX; x < maximumX; ++x) {
			for (int z = minimumZ; z < maximumZ; ++z) {
				final Faction factionAt = factionManager.getFactionAt(world, x, z);
				if (factionAt != null && !(factionAt instanceof WildernessFaction)) {
					player.sendMessage(ChatColor.RED + "This claim contains a location not within the " + ChatColor.GRAY
							+ "Wilderness" + ChatColor.RED + '.');
					return false;
				}
			}
		}
		for (int x = minimumX - 1; x < maximumX + 1; ++x) {
			for (int z = minimumZ - 1; z < maximumZ + 1; ++z) {
				final Faction factionAtNew = factionManager.getFactionAt(world, x, z);
				final boolean b = factionAtNew instanceof RoadFaction;
				if (!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
					player.sendMessage(
							ChatColor.RED + "This claim contains enemy claims within a " + 1 + " block buffer radius.");
					return false;
				}
			}
		}
		final Location minimum = claim.getMinimumPoint();
		final Location maximum = claim.getMaximumPoint();
		final Set<Claim> otherClaims = playerFaction.getClaims();
		final boolean conjoined = otherClaims.isEmpty();
		if (!conjoined) {
			player.sendMessage(ChatColor.RED + "Use /f unclaim to resize your faction claims.");
			return false;
		}
		claim.setY1(0);
		claim.setY2(256);
		if (!playerFaction.addClaim(claim, (CommandSender) player)) {
			return false;
		}
		final Location center = claim.getCenter();
		player.sendMessage(ChatColor.YELLOW + "Claim has been purchased for " + ChatColor.GREEN + '$' + claimPrice
				+ ChatColor.YELLOW + '.');
		playerFaction.setBalance(factionBalance - claimPrice);
		playerFaction.broadcast(ChatColor.GOLD + player.getName() + ChatColor.GREEN
				+ " claimed land for your faction at " + ChatColor.GOLD + '(' + center.getBlockX() + ", "
				+ center.getBlockZ() + ')' + ChatColor.GREEN + '.', player.getUniqueId());
		return true;
	}
}
