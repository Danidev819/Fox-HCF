package me.danidev.core.providers;

import java.util.ArrayList;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.nametags.BufferedNametag;
import me.danidev.core.utils.nametags.NametagAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.danidev.core.managers.classes.others.ArcherClass;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class NametagProvider implements NametagAdapter {

	@Override
	public List<BufferedNametag> getPlate(Player player) {
		List<BufferedNametag> tags = new ArrayList<>();

		for (Player players : Bukkit.getOnlinePlayers()) {
			tags.add(getNametag(player, players));
		}

		return tags;
	}

	public static BufferedNametag getNametag(Player player, Player players) {
		PlayerFaction team = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());
		BufferedNametag nametag = new BufferedNametag(players.getName(), Main.get().getMainConfig().getString("NAMETAGS.ENEMY-COLOR").toString(), "", false, false, players);

		if (player.equals(players)) {
			return new BufferedNametag(players.getName(), Main.get().getMainConfig().getString("NAMETAGS.TEAMMATE-COLOR").toString(), "", false, false, player);
		}

		if (ArcherClass.tagged.containsKey(player.getUniqueId())) {
			return new BufferedNametag(players.getName(), Main.get().getMainConfig().getString("NAMETAGS.ARCHER-TAG-COLOR").toString(), "", false, false, player);
		}

		if (team != null) {
			if (team.getMembers().keySet().contains(players.getUniqueId())) {
				return new BufferedNametag(players.getName(), Main.get().getMainConfig().getString("NAMETAGS.TEAMMATE-COLOR").toString(), "", false, false, players);
			}

			if (!team.getAlliedFactions().isEmpty()) {
				PlayerFaction targetTeam = Main.get().getFactionManager().getPlayerFaction(players.getUniqueId());

				if (team.getAlliedFactions().contains(targetTeam)) {
					return new BufferedNametag(players.getName(), ConfigurationService.ALLY_COLOR.toString(), "", false, false, players);
				}
			}
		}

		return nametag;
	}
}