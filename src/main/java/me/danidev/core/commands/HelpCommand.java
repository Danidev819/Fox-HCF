package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.command.CommandExecutor;

public class HelpCommand implements CommandExecutor {

	private final FileConfig langConfig = Main.get().getLangConfig();

	public HelpCommand(Main plugin) {
		plugin.getCommand("help").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		langConfig.getStringList("HELP").forEach(lines -> sender.sendMessage(CC.translate(lines)));
		return true;
	}
}
