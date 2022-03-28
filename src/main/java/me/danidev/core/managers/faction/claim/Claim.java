package me.danidev.core.managers.faction.claim;

import java.util.Collection;
import java.util.ArrayList;

import me.danidev.core.Main;
import me.danidev.core.managers.drops.CaseInsensitiveMap;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.utils.cuboid.Cuboid;
import me.danidev.core.utils.cuboid.NamedCuboid;
import me.danidev.core.utils.others.GenericUtils;
import org.bukkit.World;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.Random;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Claim extends NamedCuboid implements Cloneable, ConfigurationSerializable {

	private static final Random RANDOM;
	private final UUID claimUniqueID;
	private final UUID factionUUID;
	private final Map subclaims;
	private Faction faction;
	private boolean loaded;

	static {
		RANDOM = new Random();
	}

	public Claim(Map map) {
		super(map);
		this.subclaims = new CaseInsensitiveMap();
		this.loaded = false;
		this.name = (String) map.get("name");
		this.claimUniqueID = UUID.fromString((String) map.get("claimUUID"));
		this.factionUUID = UUID.fromString((String) map.get("factionUUID"));
		for (Subclaim subclaim : GenericUtils.createList(map.get("subclaims"), Subclaim.class)) {
			this.subclaims.put(subclaim.getName(), subclaim);
		}
	}

	public Claim(Faction faction, Location location) {
		super(location, location);
		this.subclaims = new CaseInsensitiveMap();
		this.loaded = false;
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, Location location1, Location location2) {
		super(location1, location2);
		this.subclaims = new CaseInsensitiveMap();
		this.loaded = false;
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		super(world, x1, y1, z1, x2, y2, z2);
		this.subclaims = new CaseInsensitiveMap();
		this.loaded = false;
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Claim(Faction faction, Cuboid cuboid) {
		super(cuboid);
		this.subclaims = new CaseInsensitiveMap();
		this.loaded = false;
		this.name = this.generateName();
		this.factionUUID = faction.getUniqueID();
		this.claimUniqueID = UUID.randomUUID();
	}

	public Map<String, Object> serialize() {
		Map map = super.serialize();
		map.put("name", this.name);
		map.put("claimUUID", this.claimUniqueID.toString());
		map.put("factionUUID", this.factionUUID.toString());
		map.put("subclaims", new ArrayList(this.subclaims.values()));
		return (Map<String, Object>) map;
	}

	private String generateName() {
		return String.valueOf(Claim.RANDOM.nextInt(899) + 100);
	}

	public UUID getClaimUniqueID() {
		return this.claimUniqueID;
	}

	public ClaimableFaction getFaction() {
		if (!this.loaded && this.faction == null) {
			this.faction = Main.get().getFactionManager().getFaction(this.factionUUID);
			this.loaded = true;
		}
		return (this.faction instanceof ClaimableFaction) ? ((ClaimableFaction) this.faction) : null;
	}

	public Collection<Subclaim> getSubclaims() {
		return this.subclaims.values();
	}

	public Subclaim getSubclaim(String name) {
		return (Subclaim) this.subclaims.get(name);
	}

	public String getFormattedName() {
		return String.valueOf(this.getName()) + ": (" + this.worldName + ", " + this.x1 + ", " + this.y1 + ", "
				+ this.z1 + ") - (" + this.worldName + ", " + this.x2 + ", " + this.y2 + ", " + this.z2 + ')';
	}

	public Claim clone() {
		return (Claim) super.clone();
	}
}
