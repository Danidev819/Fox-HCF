package me.danidev.core.commands.teleport;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportCommand implements CommandExecutor {

    public TeleportCommand(Main plugin) {
        plugin.getCommand("teleport").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 4) {
            sender.sendMessage(CC.translate("&cUsage: /" + label + " (<playerName> [otherPlayerName]) | (x y z)"));
            return true;
        }

        Player targetA;

        if (args.length == 1 || args.length == 3) {
            if (!(sender instanceof Player)) {
            	sender.sendMessage(CC.translate("&cUsage: /" + label + " (<playerName> [otherPlayerName]) | (x y z)"));
                return true;
            }
            targetA = (Player) sender;
        } else {
            targetA = BukkitUtils.playerWithNameOrUUID(args[0]);
        }
        if (targetA == null || !((Player) sender).canSee(targetA)) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }
        if (args.length < 3) {
            Player targetB = BukkitUtils.playerWithNameOrUUID(args[args.length - 1]);
            if (targetB == null || !((Player) sender).canSee(targetB)) {
                sender.sendMessage(ChatColor.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[args.length - 1] + ChatColor.GOLD + "' not found.");
                return true;
            }
            if (targetA.equals(targetB)) {
                sender.sendMessage(ChatColor.RED + "The teleport and teleported are the same player.");
                return true;
            }
            if (targetA.teleport(targetB, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                sender.sendMessage(ChatColor.YELLOW + "Teleported " + targetA.getName() + " to " + targetB.getName() + '.');
            }
            else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport you to " + targetB.getName() + '.');
            }
        }
        else if (targetA.getWorld() != null) {
            Location targetALocation = targetA.getLocation();
            double x = this.getCoordinate(sender, targetALocation.getX(), args[args.length - 3]);
            double y = this.getCoordinate(sender, targetALocation.getY(), args[args.length - 2], 0, 0);
            double z = this.getCoordinate(sender, targetALocation.getZ(), args[args.length - 1]);
            if (x == -3.0000001E7 || y == -3.0000001E7 || z == -3.0000001E7) {
                sender.sendMessage("Please provide a valid location.");
                return true;
            }
            targetALocation.setX(x);
            targetALocation.setY(y);
            targetALocation.setZ(z);
            if (targetA.teleport(targetALocation, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                sender.sendMessage(CC.translate("&eTeleported " + targetA.getName() + " to " + x + " " + y + " " + z));
            }
            else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport you.");
            }
        }
        return true;
    }

    private double getCoordinate(CommandSender sender, double current, String input) {
        return this.getCoordinate(sender, current, input, -30000000, 30000000);
    }

    private double getCoordinate(CommandSender sender, double current, String input, int min, int max) {
        boolean relative = input.startsWith("~");
        double result = relative ? current : 0.0;
        if (!relative || input.length() > 1) {
            boolean exact = input.contains(".");
            if (relative) {
                input = input.substring(1);
            }
            double testResult = VanillaCommand.getDouble(sender, input);
            if (testResult == -3.0000001E7) {
                return -3.0000001E7;
            }
            result += testResult;
            if (!exact && !relative) {
                result += 0.5;
            }
        }
        if (min != 0 || max != 0) {
            if (result < min) {
                result = -3.0000001E7;
            }
            if (result > max) {
                result = -3.0000001E7;
            }
        }
        return result;
    }
}
