package me.danidev.core.utils.visualise;

import java.util.Collections;
import java.util.ArrayList;

import me.danidev.core.utils.cuboid.Cuboid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.HashSet;
import java.util.LinkedHashMap;
import com.google.common.collect.Maps;
import com.google.common.base.Predicate;
import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import com.google.common.collect.HashBasedTable;
import org.bukkit.Location;
import java.util.UUID;
import com.google.common.collect.Table;

public class VisualiseHandler {

	private final Table<UUID, Location, VisualBlock> storedVisualises;

	public VisualiseHandler() {
		this.storedVisualises = HashBasedTable.create();
	}

	public Table<UUID, Location, VisualBlock> getStoredVisualises() {
		return this.storedVisualises;
	}

	@Deprecated
	public VisualBlock getVisualBlockAt(final Player player, final int x, final int y, final int z)
			throws NullPointerException {
		return this.getVisualBlockAt(player, new Location(player.getWorld(), (double) x, (double) y, (double) z));
	}

	public VisualBlock getVisualBlockAt(final Player player, final Location location) throws NullPointerException {
		Preconditions.checkNotNull(player, "Player cannot be null");
		Preconditions.checkNotNull(location, "Location cannot be null");
		final Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized (table) {
			// monitorexit(table)
			return (VisualBlock) this.storedVisualises.get(player.getUniqueId(), location);
		}
	}

	public Map<Location, VisualBlock> getVisualBlocks(final Player player) {
		final Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized (table) {
			// monitorexit(table)
			return new HashMap<Location, VisualBlock>(this.storedVisualises.row(player.getUniqueId()));
		}
	}

	public Map<Location, VisualBlock> getVisualBlocks(final Player player, final VisualType visualType) {
		return (Map<Location, VisualBlock>) Maps.filterValues((Map) this.getVisualBlocks(player),
				(Predicate) new Predicate<VisualBlock>() {
					public boolean apply(final VisualBlock visualBlock) {
						return visualType == visualBlock.getVisualType();
					}
				});
	}

	public LinkedHashMap<Location, VisualBlockData> generate(final Player player, final Cuboid cuboid,
			final VisualType visualType, final boolean canOverwrite) {
		final HashSet<Location> locations = new HashSet<Location>(
				cuboid.getSizeX() * cuboid.getSizeY() * cuboid.getSizeZ());
		for (final Block block : cuboid) {
			locations.add(block.getLocation());
		}
		return this.generate(player, locations, visualType, canOverwrite);
	}

	public LinkedHashMap<Location, VisualBlockData> generate(final Player player, final Iterable<Location> locations,
			final VisualType visualType, final boolean canOverwrite) {
		final Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized (table) {
			final LinkedHashMap<Location, VisualBlockData> results = new LinkedHashMap<Location, VisualBlockData>();
			final ArrayList<VisualBlockData> filled = visualType.blockFiller().bulkGenerate(player, locations);
			if (filled != null) {
				int count = 0;
				for (final Location location : locations) {
					final Material previousType;
					if ((canOverwrite || !this.storedVisualises.contains(player.getUniqueId(), location))
							&& !(previousType = location.getBlock().getType()).isSolid()) {
						if (previousType != Material.AIR) {
							continue;
						}
						final VisualBlockData visualBlockData = filled.get(count++);
						results.put(location, visualBlockData);
						player.sendBlockChange(location, visualBlockData.getBlockType(), visualBlockData.getData());
						this.storedVisualises.put(player.getUniqueId(), location,
								new VisualBlock(visualType, visualBlockData, location));
					}
				}
			}
			// monitorexit(table)
			return results;
		}
	}

	public boolean clearVisualBlock(final Player player, final Location location) {
		return this.clearVisualBlock(player, location, true);
	}

	public boolean clearVisualBlock(final Player player, final Location location, final boolean sendRemovalPacket) {
		final Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized (table) {
			final VisualBlock visualBlock = (VisualBlock) this.storedVisualises.remove(player.getUniqueId(), location);
			if (sendRemovalPacket && visualBlock != null) {
				final Block block = location.getBlock();
				final VisualBlockData visualBlockData = visualBlock.getBlockData();
				if (visualBlockData.getBlockType() != block.getType() || visualBlockData.getData() != block.getData()) {
					player.sendBlockChange(location, block.getType(), block.getData());
				}
				// monitorexit(table)
				return true;
			}
		}
		// monitorexit(table)
		return false;
	}

	public Map<Location, VisualBlock> clearVisualBlocks(final Player player) {
		return this.clearVisualBlocks(player, null, null);
	}

	public Map<Location, VisualBlock> clearVisualBlocks(final Player player, final VisualType visualType,
			final Predicate<VisualBlock> predicate) {
		return this.clearVisualBlocks(player, visualType, predicate, true);
	}

	@Deprecated
	public Map<Location, VisualBlock> clearVisualBlocks(final Player player, final VisualType visualType,
			final Predicate<VisualBlock> predicate, final boolean sendRemovalPackets) {
		synchronized (this.storedVisualises) {
			if (!this.storedVisualises.containsRow(player.getUniqueId())) {
				// monitorexit(this.storedVisualises)
				return Collections.emptyMap();
			}
			final Map<Location, VisualBlock> results = new HashMap<Location, VisualBlock>(
					this.storedVisualises.row(player.getUniqueId()));
			final Map<Location, VisualBlock> removed = new HashMap<Location, VisualBlock>();
			for (final Map.Entry<Location, VisualBlock> entry : results.entrySet()) {
				final VisualBlock visualBlock = entry.getValue();
				if ((predicate == null || predicate.apply(visualBlock))
						&& (visualType == null || visualBlock.getVisualType() == visualType)) {
					final Location location = entry.getKey();
					if (removed.put(location, visualBlock) != null) {
						continue;
					}
					this.clearVisualBlock(player, location, sendRemovalPackets);
				}
			}
			// monitorexit(this.storedVisualises)
			return removed;
		}
	}
}
