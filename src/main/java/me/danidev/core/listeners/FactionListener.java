package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.base.Optional;

import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.List;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.event.EventPriority;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import me.danidev.core.managers.faction.event.CaptureZoneEnterEvent;
import me.danidev.core.managers.faction.event.CaptureZoneLeaveEvent;
import me.danidev.core.managers.faction.event.FactionCreateEvent;
import me.danidev.core.managers.faction.event.FactionRemoveEvent;
import me.danidev.core.managers.faction.event.FactionRenameEvent;
import me.danidev.core.managers.faction.event.PlayerClaimEnterEvent;
import me.danidev.core.managers.faction.event.PlayerJoinFactionEvent;
import me.danidev.core.managers.faction.event.PlayerLeaveFactionEvent;
import me.danidev.core.managers.faction.event.PlayerLeftFactionEvent;
import me.danidev.core.managers.faction.struct.RegenStatus;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.faction.type.SpawnFaction;

import org.bukkit.event.Listener;

public class FactionListener implements Listener {

	private final FileConfig langConfig = Main.get().getLangConfig();

	public FactionListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionCreate(FactionCreateEvent event) {
		if (!(event.getSender() instanceof Player)) return;

		Player player = (Player) event.getSender();

		Faction faction = event.getFaction();
		String playerName = Main.get().getRankManager().getRank().getPrefix(player) + player.getName();

		if (faction instanceof PlayerFaction) {
			Bukkit.broadcastMessage(CC.translate(langConfig.getString("FACTION.CREATE")
					.replace("%PLAYER%", playerName)
					.replace("%FACTION%", faction.getName())));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRemove(FactionRemoveEvent event) {
		if (!(event.getSender() instanceof Player)) return;

		Player player = (Player) event.getSender();

		Faction faction = event.getFaction();
		String playerName = Main.get().getRankManager().getRank().getPrefix(player) + player.getName();

		if (faction instanceof PlayerFaction) {
			Bukkit.broadcastMessage(CC.translate(langConfig.getString("FACTION.DISBAND")
					.replace("%PLAYER%", playerName)
					.replace("%FACTION%", faction.getName())));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRename(FactionRenameEvent event) {
		if (!(event.getSender() instanceof Player)) return;

		Player player = (Player) event.getSender();

		Faction faction = event.getFaction();
		String playerName = Main.get().getRankManager().getRank().getPrefix(player) + player.getName();

		if (faction instanceof PlayerFaction) {
			Bukkit.broadcastMessage(CC.translate(langConfig.getString("FACTION.RENAME")
					.replace("%PLAYER%", playerName)
					.replace("%FACTION%", faction.getName())
					.replace("%NEW_NAME%", event.getNewName())));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRenameMonitor(FactionRenameEvent event) {
		Faction faction = event.getFaction();

		if (faction instanceof KothFaction) {
			((KothFaction) faction).getCaptureZone().setName(event.getNewName());
		}
	}

	private long getLastLandChangedMeta(Player player) {
		List<MetadataValue> value = player.getMetadata("landChangedMessage");

		long millis = System.currentTimeMillis();
		long remaining = (value == null || value.isEmpty()) ? 0L : (value.get(0).asLong() - millis);

		if (remaining <= 0L) {
			player.setMetadata("landChangedMessage", new FixedMetadataValue(Main.get(), (millis + 225L)));
		}

		return remaining;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
		Player player = event.getPlayer();

		if (this.getLastLandChangedMeta(player) <= 0L
				&& Main.get().getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
			player.sendMessage(ChatColor.YELLOW + "Now entering capture zone: " + event.getCaptureZone().getDisplayName()
					+ ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
		Player player = event.getPlayer();

		if (this.getLastLandChangedMeta(player) <= 0L && Main.get().getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
			player.sendMessage(ChatColor.YELLOW + "Now leaving capture zone: " + event.getCaptureZone().getDisplayName()
					+ ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
		Faction toFaction = event.getToFaction();
		Player player = event.getPlayer();

		if (toFaction.isSafezone()) {
			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.setFireTicks(0);
			player.setSaturation(4.0F);
		}

		if (this.getLastLandChangedMeta(player) <= 0L) {
			if (Main.get().isKitMap()) {
				Faction fromFaction = event.getFromFaction();

				player.sendMessage(CC.translate(langConfig.getString("CLAIM.KITMAP.ENTER")
						.replace("%CLAIM%", toFaction.getDisplayName(player))));
				player.sendMessage(CC.translate(langConfig.getString("CLAIM.KITMAP.LEAVE")
						.replace("%CLAIM%", fromFaction.getDisplayName(player))));
			}
			else {
				Faction fromFaction = event.getFromFaction();

				player.sendMessage(CC.translate(langConfig.getString("CLAIM.HCF.ENTER")
						.replace("%CLAIM%", toFaction.getDisplayName(player))
						.replace("%DEATHBAN%", toFaction.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban")));
				player.sendMessage(CC.translate(langConfig.getString("CLAIM.HCF.LEAVE")
						.replace("%CLAIM%", fromFaction.getDisplayName(player))
						.replace("%DEATHBAN%", fromFaction.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban")));
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onSpawnFoodChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		Faction faction = Main.get().getFactionManager().getFactionAt(player.getLocation());

		if (faction instanceof SpawnFaction) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
		Optional<Player> optionalPlayer = event.getPlayer();

		if (optionalPlayer.isPresent()) {
			Main.get().getUserManager().getUser((optionalPlayer.get()).getUniqueId())
					.setLastFactionLeaveMillis(System.currentTimeMillis());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
		Faction faction = event.getFaction();
		Optional<Player> optionalPlayer = event.getPlayer();

		if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
			Player player = optionalPlayer.get();
			PlayerFaction playerFaction = (PlayerFaction) faction;

			if (playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionLeave(PlayerLeaveFactionEvent event) {
		Faction faction = event.getFaction();
		Optional<Player> optional;

		if (faction instanceof PlayerFaction && (optional = event.getPlayer()).isPresent()) {
			Player player = optional.get();

			if (Main.get().getFactionManager().getFactionAt(player.getLocation()).equals(faction)) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot leave your faction whilst you remain in its' territory.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

		if (playerFaction != null) {
			playerFaction.printDetails(player);
			playerFaction.broadcast(CC.translate(langConfig.getString("FACTION.CONNECTED")
					.replace("%PLAYER%", playerFaction.getMember(player).getRole().getAstrix() + player.getName())));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

		if (playerFaction != null) {
			playerFaction.broadcast(CC.translate(langConfig.getString("FACTION.DISCONNECTED")
					.replace("%PLAYER%", playerFaction.getMember(player).getRole().getAstrix() + player.getName())));
		}
	}
}
