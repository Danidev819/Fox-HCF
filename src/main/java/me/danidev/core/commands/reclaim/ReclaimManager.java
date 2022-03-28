package me.danidev.core.commands.reclaim;

import me.danidev.core.Main;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.configuration.MemorySection;
import java.util.Set;

public class ReclaimManager implements Reclaim {

	private final FileConfig reclaimConfig = Main.get().getReclaimConfig();
	private final FileConfig langConfig = Main.get().getLangConfig();

	@Override
	public void getReclaim(Player player) {
		FactionUser user = Main.get().getUserManager().getUser(player.getUniqueId());

		if (user.isReclaimed()) {
			player.sendMessage(CC.translate(langConfig.getString("RECLAIM.ALREADY")));
			return;
		}

		String group = null;

		for (String key : this.getGroup()) {
			if (player.hasPermission("fhcf.reclaim." + key)) {
				group = key;
			}
		}

		if (group == null) {
			player.sendMessage(CC.translate(langConfig.getString("RECLAIM.NOTHING")));
			return;
		}

		String finalGroup = group;

		reclaimConfig.getStringList("RECLAIM." + group + ".COMMANDS").forEach(command ->
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
					.replace("%PLAYER%", player.getName())
					.replace("%GROUP%", finalGroup)));

		user.setReclaimed(true);
		player.sendMessage(CC.translate(langConfig.getString("RECLAIM.CLAIM")));
	}

	public Set<String> getGroup() {
		if (reclaimConfig.getConfiguration().get("RECLAIM") != null) {
			Object object = reclaimConfig.getConfiguration().get("RECLAIM");
			if (object instanceof MemorySection) {
				MemorySection section = (MemorySection) object;
				return section.getKeys(false);
			}
		}
		return null;
	}
}
