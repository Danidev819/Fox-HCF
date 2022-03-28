package me.danidev.core.commands.lives.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesCheckArgument extends CommandArgument {

	public LivesCheckArgument() {
		super("check", "Check Lives");
		this.permission = "fhcf.command.lives.argument." + this.getName();
	}

	@Override
	public String getUsage(final String label) {
		return "/" + label + ' ' + this.getName() + " [playerName]";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if (args.length < 1) {
			int playerLives = Main.get().getLivesManager().getLives(player.getUniqueId());
			player.sendMessage(CC.translate("&eYou've &6" + playerLives + " &e" + ((playerLives == 1) ? "life" : "lives") + "."));
			return true;
		}

		Player target = Bukkit.getPlayer(args[1]);

		if (target == null) {
			sender.sendMessage(CC.translate("&cPlayer not found."));
			return true;
		}

		int targetLives = Main.get().getLivesManager().getLives(target.getUniqueId());
		player.sendMessage(CC.translate("&e" + target.getName() + " has &6" + targetLives + " &e" + ((targetLives == 1) ? "life" : "lives") + "."));
		return true;
	}
}
