package me.danidev.core.commands.lives.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesClearDeathbanArgument extends CommandArgument {

	public LivesClearDeathbanArgument() {
		super("cleardeathbans", "Clears the global deathbans");
		this.permission = "fhcf.command.lives.argument." + this.getName();
	}

	@Override
	public String getUsage(String label) {
		return "/" + label + ' ' + this.getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (FactionUser user : Main.get().getUserManager().getUsers().values()) {
			user.removeDeathban();
		}
		sender.sendMessage(CC.translate("&eAll deathbans have been cleared."));
		return false;
	}
}
