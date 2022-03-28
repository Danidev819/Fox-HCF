package me.danidev.core.commands.death.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.deathban.Deathban;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.command.CommandArgument;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathInfoArgument extends CommandArgument {
    
    public DeathInfoArgument() {
        super("info", "Check Deathban Reason");
        this.permission = "fhcf.command.death.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName() + " <player>";
    }

    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + this.getUsage(command.getLabel()));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        FactionUser targetUser = Main.get().getUserManager().getUser(target.getUniqueId());
        Deathban deathban = targetUser.getDeathban();

        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + "This player is not deathbanned.");
            return true;
        }

        Double x = deathban.getDeathLocation().getX();
        Double y = deathban.getDeathLocation().getY();
        Double z = deathban.getDeathLocation().getZ();
        String remaining = DurationFormatter.getRemaining(deathban.getExpiryMillis(), true, false);
        DecimalFormat decimalFormat = new DecimalFormat("##");

        sender.sendMessage(CC.translate("&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        sender.sendMessage(CC.translate("&6&lDeath Info"));
        sender.sendMessage(CC.translate(" &eStatus: &cDeathbanned."));
        sender.sendMessage(CC.translate(" &eReason: &f" + deathban.getReason() + "&e."));
        sender.sendMessage(CC.translate(" &eRemaining: &f" + remaining + "&e."));
        sender.sendMessage(CC.translate(" &eLocation: &f"
                + decimalFormat.format(x) + ", "
                + decimalFormat.format(y) + ", "
                + decimalFormat.format(z) + "&e."));
        sender.sendMessage(CC.translate("&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        return true;
    }
}
