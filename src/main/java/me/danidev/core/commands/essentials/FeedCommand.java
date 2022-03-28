package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

	private final FileConfig langConfig = Main.get().getLangConfig();

	public FeedCommand(Main plugin) {
		plugin.getCommand("feed").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		player.setFoodLevel(20);
		player.sendMessage(CC.translate(langConfig.getString("FEED")));
		return true;
	}
}
