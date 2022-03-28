package me.danidev.core.managers.user;

import me.danidev.core.utils.others.GenericUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class ServerParticipator implements ConfigurationSerializable {

	private final UUID uniqueId;
	private final Set<String> ignoring;
	private UUID lastRepliedTo;
	private boolean messagesVisible;

	public ServerParticipator(final UUID uniqueId) {
		this.ignoring = (Set<String>) Sets.newTreeSet((Comparator) String.CASE_INSENSITIVE_ORDER);
		this.messagesVisible = true;
		this.uniqueId = uniqueId;
	}

	public ServerParticipator(final Map<String, Object> map) {
		this.ignoring = (Set<String>) Sets.newTreeSet((Comparator) String.CASE_INSENSITIVE_ORDER);
		this.messagesVisible = true;
		this.uniqueId = UUID.fromString((String) map.get("uniqueID"));
		this.ignoring.addAll(GenericUtils.createList(map.get("ignoring"), (Class) String.class));

		Object object = map.get("lastRepliedTo");

		if (object instanceof String) {
			this.lastRepliedTo = UUID.fromString((String) object);
		}
		if ((object = map.get("messagesVisible")) instanceof Boolean) {
			this.messagesVisible = (boolean) object;
		}
	}

	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("uniqueID", this.uniqueId.toString());
		map.put("ignoring", new ArrayList(this.ignoring));
		if (this.lastRepliedTo != null) {
			map.put("lastRepliedTo", this.lastRepliedTo.toString());
		}
		map.put("messagesVisible", this.messagesVisible);
		return map;
	}

	public abstract String getName();

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public Set<String> getIgnoring() {
		return this.ignoring;
	}

	public UUID getLastRepliedTo() {
		return this.lastRepliedTo;
	}

	public void setLastRepliedTo(final UUID lastRepliedTo) {
		this.lastRepliedTo = lastRepliedTo;
	}

	public Player getLastRepliedToPlayer() {
		return Bukkit.getPlayer(this.lastRepliedTo);
	}

	public boolean isMessagesVisible() {
		return this.messagesVisible;
	}

	public void setMessagesVisible(final boolean messagesVisible) {
		this.messagesVisible = messagesVisible;
	}
}
