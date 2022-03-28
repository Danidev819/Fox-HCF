package me.danidev.core.managers.user;

import me.danidev.core.utils.others.GenericUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import com.google.common.collect.Maps;
import me.danidev.core.managers.deathban.Deathban;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

import java.util.Map;
import java.util.HashSet;
import java.util.UUID;
import java.util.Set;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class FactionUser implements ConfigurationSerializable {

	private final Set<String> shownScoreboardScores;
	private final UUID userUUID;
	private boolean reclaimed;
	private boolean capzoneEntryAlerts;
	private boolean showClaimMap;
	private Deathban deathban;
	private PlayerFaction playerFaction;
	private Faction faction;
	private long lastFactionLeaveMillis;
	private int kills;
	private int deaths;
	private int keys;
	private int diamondsMined;

	public FactionUser(UUID userUUID) {
		this.shownScoreboardScores = new HashSet<>();
		this.userUUID = userUUID;
	}

	public FactionUser(Map<String, Object> map) {
		this.shownScoreboardScores = new HashSet<>();
		this.shownScoreboardScores.addAll(GenericUtils.createList(map.get("shownScoreboardScores"), String.class));
		this.userUUID = UUID.fromString((String) map.get("userUUID"));
		this.capzoneEntryAlerts = (boolean) map.get("capzoneEntryAlerts");
		this.deathban = (Deathban) map.get("deathban");
		this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
		this.diamondsMined = (int) map.get("diamonds");
		this.kills = (int) map.get("kills");
		this.deaths = (int) map.get("deaths");
		this.keys = (int) map.get("keys");
		this.reclaimed = (boolean) map.getOrDefault("reclaimed", false);
	}

	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		map.put("shownScoreboardScores", new ArrayList<>(this.shownScoreboardScores));
		map.put("userUUID", this.userUUID.toString());
		map.put("diamonds", this.diamondsMined);
		map.put("capzoneEntryAlerts", this.capzoneEntryAlerts);
		map.put("showClaimMap", this.showClaimMap);
		map.put("deathban", this.deathban);
		map.put("lastFactionLeaveMillis", Long.toString(this.lastFactionLeaveMillis));
		map.put("kills", this.kills);
		map.put("deaths", this.deaths);
		map.put("keys", this.keys);
		map.put("reclaimed", this.reclaimed);
		return map;
	}

	public boolean isCapzoneEntryAlerts() {
		return this.capzoneEntryAlerts;
	}

	public boolean isShowClaimMap() {
		return this.showClaimMap;
	}

	public void setShowClaimMap(final boolean showClaimMap) {
		this.showClaimMap = showClaimMap;
	}

	public int getKills() {
		return this.kills;
	}

	public void setKills(final int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return this.deaths;
	}

	public void setDeaths(final int deaths) {
		this.deaths = deaths;
	}

	public int getKeys() {
		return this.keys;
	}

	public void setKeys(final int keys) {
		this.keys = keys;
	}

	public int getDiamondsMined() {
		return this.diamondsMined;
	}

	public void setDiamondsMined(final int diamondsMined) {
		this.diamondsMined = diamondsMined;
	}

	public Deathban getDeathban() {
		return this.deathban;
	}

	public void setDeathban(final Deathban deathban) {
		this.deathban = deathban;
	}

	public void removeDeathban() {
		this.deathban = null;
	}

	public long getLastFactionLeaveMillis() {
		return this.lastFactionLeaveMillis;
	}

	public void setLastFactionLeaveMillis(final long lastFactionLeaveMillis) {
		this.lastFactionLeaveMillis = lastFactionLeaveMillis;
	}

	public Set<String> getShownScoreboardScores() {
		return this.shownScoreboardScores;
	}

	public UUID getUserUUID() {
		return this.userUUID;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.userUUID);
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(this.userUUID);
	}

	public boolean isReclaimed() {
		return this.reclaimed;
	}

	public void setReclaimed(final boolean reclaimed) {
		this.reclaimed = reclaimed;
	}

	public PlayerFaction getPlayerFaction() {
		return this.playerFaction;
	}

	public Faction getFaction() {
		return this.faction;
	}
}
