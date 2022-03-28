package me.danidev.core.commands;

import me.danidev.core.Main;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

import me.danidev.core.utils.CC;

public class FFACommand implements CommandExecutor {

    public FFACommand(Main plugin) {
        plugin.getCommand("ffa").setExecutor(this);
    }
	
    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(CC.translate("&cUsage: /" + label + " <effects|uneffects>"));
            return true;
        }

        if (args[0].equalsIgnoreCase("effects")) {
            Bukkit.getOnlinePlayers().forEach(online -> this.effects(online, true));
            sender.sendMessage(CC.translate("&eYou have given all players potion effects."));
        }
        else if (args[0].equalsIgnoreCase("uneffects")) {
            Bukkit.getOnlinePlayers().forEach(online -> this.effects(online, false));
            sender.sendMessage(CC.translate("&eYou've remove all players potion effects."));
        }
        else {
            sender.sendMessage(CC.translate("&cArgument '" + args[0] + "' not found."));
        }
        return true;
    }

    private void effects(Player player, boolean give) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        if (give) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        }
    }
}
