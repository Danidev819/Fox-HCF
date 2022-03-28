package me.danidev.core.commands.death.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.deathban.Deathban;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeathReviveArgument extends CommandArgument {

    public DeathReviveArgument() {
        super("revive", "Revive a deathbanned player");
        this.permission = "fhcf.commands.death.argument." + this.getName();
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

        FactionUser factionTarget = Main.get().getUserManager().getUser(target.getUniqueId());
        Deathban deathban = factionTarget.getDeathban();

        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + "Player is not deathbanned.");
            return true;
        }

        factionTarget.removeDeathban();
        sender.sendMessage(CC.translate("&eYou have revived " + target.getName() + "."));
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return Collections.emptyList();

        List<String> results = new ArrayList<>();

        for (FactionUser factionUser : Main.get().getUserManager().getUsers().values()) {
            Deathban deathban = factionUser.getDeathban();

            if (deathban != null && deathban.isActive()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                String name = offlinePlayer.getName();

                if (name == null) continue;

                results.add(name);
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }
}
