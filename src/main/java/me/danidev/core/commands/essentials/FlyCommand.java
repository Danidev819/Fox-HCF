package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

	private final FileConfig langConfig = Main.get().getLangConfig();

	public FlyCommand(Main plugin) {
		plugin.getCommand("fly").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if (player.getAllowFlight()) {
			player.setAllowFlight(false);
			player.sendMessage(CC.translate(langConfig.getString("FLY.DISABLED")));
		}
		else {
			player.setAllowFlight(true);
			player.sendMessage(CC.translate(langConfig.getString("FLY.ENABLED")));
		}
		return true;
	}
}
