package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Utils;

import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class SetCommand implements CommandExecutor {

	private final FileConfig locationsConfig = Main.get().getLocationsConfig();

	public SetCommand(Main plugin) {
		plugin.getCommand("set").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(CC.translate(CC.CHAT_BAR));
			player.sendMessage(CC.translate("&6&lSet Command"));
			player.sendMessage(CC.translate(""));
			player.sendMessage(CC.translate("&7/set endspawn &7- &fCreate location for spawn end."));
			player.sendMessage(CC.translate("&7/set endexit &7- &fCreate location for exit end."));
			player.sendMessage(CC.translate("&7/set spawn &7- &fCreate location for exit end."));
			player.sendMessage(CC.translate(CC.CHAT_BAR));
			return true;
		}

		if(args[0].equalsIgnoreCase("endspawn")) {
			locationsConfig.getConfiguration().set("END_SPAWN.X", player.getLocation().getX());
			locationsConfig.getConfiguration().set("END_SPAWN.Y", player.getLocation().getY());
			locationsConfig.getConfiguration().set("END_SPAWN.Z", player.getLocation().getZ());
			locationsConfig.getConfiguration().set("END_SPAWN.YAW", player.getLocation().getYaw());
			locationsConfig.getConfiguration().set("END_SPAWN.PITCH", player.getLocation().getPitch());
			locationsConfig.getConfiguration().set("END_SPAWN.WORLD", player.getLocation().getWorld().getName());
			locationsConfig.save();
			player.sendMessage(CC.translate("&aSuccessfully created endspawn."));
		}
		if (args[0].equalsIgnoreCase("endexit")) {
			locationsConfig.getConfiguration().set("END_EXIT.X", player.getLocation().getX());
			locationsConfig.getConfiguration().set("END_EXIT.Y", player.getLocation().getY());
			locationsConfig.getConfiguration().set("END_EXIT.Z", player.getLocation().getZ());
			locationsConfig.getConfiguration().set("END_EXIT.YAW", player.getLocation().getYaw());
			locationsConfig.getConfiguration().set("END_EXIT.PITCH", player.getLocation().getPitch());
			locationsConfig.getConfiguration().set("END_EXIT.WORLD", player.getLocation().getWorld().getName());
			locationsConfig.save();
			Main.get().getWaypointManager().createWaypoint("End Exit", player.getLocation(), Color.PURPLE.asBGR());
			player.sendMessage(CC.translate("&aSuccessfully created endexit."));
		}
		if(args[0].equalsIgnoreCase("spawn")) {
			locationsConfig.getConfiguration().set("SPAWN.X", player.getLocation().getX());
			locationsConfig.getConfiguration().set("SPAWN.Y", player.getLocation().getY());
			locationsConfig.getConfiguration().set("SPAWN.Z", player.getLocation().getZ());
			locationsConfig.getConfiguration().set("SPAWN.YAW", player.getLocation().getYaw());
			locationsConfig.getConfiguration().set("SPAWN.PITCH", player.getLocation().getPitch());
			locationsConfig.getConfiguration().set("SPAWN.WORLD", player.getLocation().getWorld().getName());
			locationsConfig.save();
			player.sendMessage(CC.translate("&aSuccessfully created spawn."));
		}
		else {
			player.sendMessage(CC.translate(CC.CHAT_BAR));
			player.sendMessage(CC.translate("&6&lSet Command"));
			player.sendMessage(CC.translate(""));
			player.sendMessage(CC.translate("&7/set endspawn &7- &fCreate location for spawn end."));
			player.sendMessage(CC.translate("&7/set endexit &7- &fCreate location for exit end."));
			player.sendMessage(CC.translate("&7/set spawn &7- &fCreate location for exit end."));
			player.sendMessage(CC.translate(CC.CHAT_BAR));
		}
		return true;
	}
}
