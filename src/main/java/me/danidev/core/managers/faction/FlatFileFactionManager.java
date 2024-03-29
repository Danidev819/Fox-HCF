package me.danidev.core.managers.faction;

import java.util.Set;

import me.danidev.core.Main;
import me.danidev.core.managers.drops.CaseInsensitiveMap;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.event.cause.ClaimChangeCause;
import me.danidev.core.managers.faction.struct.ChatChannel;
import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.user.Config;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.cuboid.CoordinatePair;

import java.util.HashSet;

import org.bukkit.configuration.MemorySection;
import com.google.common.base.Preconditions;

import org.bukkit.event.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

import java.util.UUID;

import me.danidev.core.managers.faction.event.FactionClaimChangedEvent;
import me.danidev.core.managers.faction.event.FactionCreateEvent;
import me.danidev.core.managers.faction.event.FactionRelationRemoveEvent;
import me.danidev.core.managers.faction.event.FactionRemoveEvent;
import me.danidev.core.managers.faction.event.FactionRenameEvent;
import me.danidev.core.managers.faction.event.PlayerJoinedFactionEvent;
import me.danidev.core.managers.faction.event.PlayerLeftFactionEvent;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.EndPortalFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.GlowstoneFaction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.faction.type.RoadFaction;
import me.danidev.core.managers.faction.type.SpawnFaction;
import me.danidev.core.managers.faction.type.WarzoneFaction;
import me.danidev.core.managers.faction.type.WildernessFaction;

import java.util.Map;

import org.bukkit.event.Listener;

public class FlatFileFactionManager implements Listener, FactionManager {

	private final WarzoneFaction warzone;
	private final WildernessFaction wilderness;
	private final Map<CoordinatePair, Claim> claimPositionMap;
	private final Map<UUID, UUID> factionPlayerUuidMap;
	private final Map<UUID, Faction> factionUUIDMap;
	private final Map<String, UUID> factionNameMap;
	private final Main plugin;
	private Config config;

	public FlatFileFactionManager(final Main plugin) {
		this.claimPositionMap = new HashMap<>();
		this.factionPlayerUuidMap = new ConcurrentHashMap<>();
		this.factionUUIDMap = new HashMap<>();
		this.factionNameMap = (Map<String, UUID>)new CaseInsensitiveMap();
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.warzone = new WarzoneFaction();
		this.wilderness = new WildernessFaction();
		this.reloadFactionData();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event) {
		this.factionPlayerUuidMap.put(event.getUniqueID(), event.getFaction().getUniqueID());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
		this.factionPlayerUuidMap.remove(event.getUniqueID());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRename(final FactionRenameEvent event) {
		this.factionNameMap.remove(event.getOriginalName());
		this.factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionClaim(final FactionClaimChangedEvent event) {
		for (final Claim claim : event.getAffectedClaims()) {
			this.cacheClaim(claim, event.getCause());
		}
	}

	@Deprecated
	public Map<String, UUID> getFactionNameMap() {
		return this.factionNameMap;
	}

	public List<Faction> getFactions() {
		final List<Faction> asd = new ArrayList<>();
		for (final Faction fac : this.factionUUIDMap.values()) {
			asd.add(fac);
		}
		return asd;
	}

	public Claim getClaimAt(final World world, final int x, final int z) {
		return this.claimPositionMap.get(new CoordinatePair(world, x, z));
	}

	public Claim getClaimAt(final Location location) {
		return this.getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
	}

	public Faction getFactionAt(final World world, final int x, final int z) {
		final World.Environment environment = world.getEnvironment();
		final Claim claim = this.getClaimAt(world, x, z);
		if (claim != null) {
			final Faction faction = claim.getFaction();
			if (faction != null) {
				return faction;
			}
		}
		if (environment == World.Environment.THE_END) {
			return this.warzone;
		}
		final int warzoneRadius = ConfigurationService.WARZONE_RADIUS * (ConfigurationService.BORDER_SIZES.get(environment) / ConfigurationService.BORDER_SIZES.get(World.Environment.NORMAL));
		return (Math.abs(x) > warzoneRadius || Math.abs(z) > warzoneRadius) ? this.wilderness : this.warzone;
	}

	public Faction getFactionAt(final Location location) {
		return this.getFactionAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
	}

	public Faction getFactionAt(final Block block) {
		return this.getFactionAt(block.getLocation());
	}

	public Faction getFaction(final String factionName) {
		final UUID uuid = this.factionNameMap.get(factionName);
		return (uuid == null) ? null : this.factionUUIDMap.get(uuid);
	}

	public Faction getFaction(final UUID factionUUID) {
		return this.factionUUIDMap.get(factionUUID);
	}

	public PlayerFaction getPlayerFaction(final UUID playerUUID) {
		final UUID uuid = this.factionPlayerUuidMap.get(playerUUID);
		final Faction faction = (uuid == null) ? null : this.factionUUIDMap.get(uuid);
		return (faction instanceof PlayerFaction) ? ((PlayerFaction)faction) : null;
	}

	public PlayerFaction getPlayerFaction(final Player player) {
		return this.getPlayerFaction(player.getUniqueId());
	}

	public PlayerFaction getContainingPlayerFaction(final String search) {
		final OfflinePlayer target = JavaUtils.isUUID(search) ? Bukkit.getOfflinePlayer(UUID.fromString(search)) : Bukkit.getOfflinePlayer(search);
		return (target.hasPlayedBefore() || target.isOnline()) ? this.getPlayerFaction(target.getUniqueId()) : null;
	}

	public Faction getContainingFaction(final String search) {
		final Faction faction = this.getFaction(search);
		if (faction != null) {
			return faction;
		}
		final UUID playerUUID = Bukkit.getOfflinePlayer(search).getUniqueId();
		if (playerUUID != null) {
			return this.getPlayerFaction(playerUUID);
		}
		return null;
	}

	public boolean containsFaction(final Faction faction) {
		return this.factionNameMap.containsKey(faction.getName());
	}

	public boolean createFaction(final Faction faction) {
		return this.createFaction(faction, Bukkit.getConsoleSender());
	}

	public boolean createFaction(final Faction faction, final CommandSender sender) {
		if (this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) {
			return false;
		}
		this.factionNameMap.put(faction.getName(), faction.getUniqueID());
		if (faction instanceof PlayerFaction && sender instanceof Player) {
			final Player player = (Player)sender;
			final PlayerFaction playerFaction = (PlayerFaction)faction;
			if (!playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER))) {
				return false;
			}
		}
		final FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);
		Bukkit.getPluginManager().callEvent((Event)createEvent);
		return !createEvent.isCancelled();
	}

	public boolean removeFaction(final Faction faction, final CommandSender sender) {
		if (this.factionUUIDMap.remove(faction.getUniqueID()) == null) {
			return false;
		}
		this.factionNameMap.remove(faction.getName());
		final FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
		Bukkit.getPluginManager().callEvent(removeEvent);
		if (removeEvent.isCancelled()) {
			return false;
		}
		if (faction instanceof ClaimableFaction) {
			Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, ((ClaimableFaction)faction).getClaims()));
		}
		if (faction instanceof PlayerFaction) {
			final PlayerFaction playerFaction = (PlayerFaction)faction;
			for (final PlayerFaction ally : playerFaction.getAlliedFactions()) {
				Bukkit.getPluginManager().callEvent((Event)new FactionRelationRemoveEvent(playerFaction, ally, Relation.ENEMY));
				ally.getRelations().remove(faction.getUniqueID());
			}
		}
		if (faction instanceof PlayerFaction) {
			final PlayerFaction playerFaction = (PlayerFaction)faction;
			for (final PlayerFaction ally : playerFaction.getAlliedFactions()) {
				ally.getRelations().remove(faction.getUniqueID());
			}
			for (final UUID uuid : playerFaction.getMembers().keySet()) {
				playerFaction.setMember(uuid, null, true);
			}
		}
		return true;
	}

	private void cacheClaim(final Claim claim, final ClaimChangeCause cause) {
		Preconditions.checkNotNull((Object)claim, "Claim cannot be null");
		Preconditions.checkNotNull((Object)cause, "Cause cannot be null");
		Preconditions.checkArgument(cause != ClaimChangeCause.RESIZE, (Object)"Cannot cache claims of resize others");
		final World world = claim.getWorld();
		if (world == null) {
			return;
		}
		final int minX = Math.min(claim.getX1(), claim.getX2());
		final int maxX = Math.max(claim.getX1(), claim.getX2());
		final int minZ = Math.min(claim.getZ1(), claim.getZ2());
		final int maxZ = Math.max(claim.getZ1(), claim.getZ2());
		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				final CoordinatePair coordinatePair = new CoordinatePair(world, x, z);
				if (cause == ClaimChangeCause.CLAIM) {
					this.claimPositionMap.put(coordinatePair, claim);
				}
				else if (cause == ClaimChangeCause.UNCLAIM) {
					this.claimPositionMap.remove(coordinatePair);
				}
			}
		}
	}

	private void cacheFaction(final Faction faction) {
		this.factionNameMap.put(faction.getName(), faction.getUniqueID());
		this.factionUUIDMap.put(faction.getUniqueID(), faction);
		if (faction instanceof ClaimableFaction) {
			final ClaimableFaction claimableFaction = (ClaimableFaction)faction;
			for (final Claim claim : claimableFaction.getClaims()) {
				this.cacheClaim(claim, ClaimChangeCause.CLAIM);
			}
		}
		if (faction instanceof PlayerFaction) {
			for (final FactionMember factionMember : ((PlayerFaction)faction).getMembers().values()) {
				this.factionPlayerUuidMap.put(factionMember.getUniqueId(), faction.getUniqueID());
			}
		}
	}

	public void reloadFactionData() {
		this.factionNameMap.clear();
		this.config = new Config(this.plugin, "factions");
		final Object object = this.config.get("factions");
		if (object instanceof MemorySection) {
			final MemorySection section = (MemorySection)object;
			for (final String factionName : section.getKeys(false)) {
				final Object next = this.config.get(String.valueOf(section.getCurrentPath()) + '.' + factionName);
				if (next instanceof Faction) {
					this.cacheFaction((Faction)next);
				}
			}
		}
		else if (object instanceof List) {
			final List list = (List)object;
			for (final Object next2 : list) {
				if (next2 instanceof Faction) {
					this.cacheFaction((Faction)next2);
				}
			}
		}
		final Set<Faction> adding = new HashSet<>();
		if (!this.factionNameMap.containsKey("Warzone")) {
			adding.add(new WarzoneFaction());
		}
		if (!this.factionNameMap.containsKey("Glowstone")) {
			adding.add(new GlowstoneFaction());
		}
		if (!this.factionNameMap.containsKey("Spawn")) {
			adding.add(new SpawnFaction());
		}
		if (!this.factionNameMap.containsKey("NorthRoad")) {
			adding.add(new RoadFaction.NorthRoadFaction());
		}
		if (!this.factionNameMap.containsKey("EastRoad")) {
			adding.add(new RoadFaction.EastRoadFaction());
		}
		if (!this.factionNameMap.containsKey("WestRoad")) {
			adding.add(new RoadFaction.WestRoadFaction());
		}
		if (!this.factionNameMap.containsKey("SouthRoad")) {
			adding.add(new RoadFaction.SouthRoadFaction());
		}
		if (!this.factionNameMap.containsKey("EndPortal")) {
			adding.add(new EndPortalFaction.EndPortalFaction1());
		}
		if (!this.factionNameMap.containsKey("EndPortal")) {
			adding.add(new EndPortalFaction.EndPortalFaction2());
		}
		if (!this.factionNameMap.containsKey("EndPortal")) {
			adding.add(new EndPortalFaction.EndPortalFaction3());
		}
		if (!this.factionNameMap.containsKey("EndPortal")) {
			adding.add(new EndPortalFaction.EndPortalFaction4());
		}
		if (!this.factionNameMap.containsKey("Wilderness")) {
			adding.add(new WildernessFaction());
		}
		for (final Faction added : adding) {
			this.cacheFaction(added);
		}
	}

	public void saveFactionData() {
		this.config.set("factions", new ArrayList(this.factionUUIDMap.values()));
		this.config.save();
	}
}
