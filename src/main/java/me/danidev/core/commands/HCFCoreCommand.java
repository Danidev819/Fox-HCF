package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HCFCoreCommand implements CommandExecutor {

    private Plugin plugin;

    private void reloadFiles() {
        Main.get().getScoreboardConfig().reload();
        Main.get().getMainConfig().reload();
        Main.get().getLangConfig().reload();
        Main.get().getAbilitiesConfig().reload();
        Main.get().getBlockShopConfig().reload();
        Main.get().getReclaimConfig().reload();
        Main.get().getSupportConfig().reload();
        Main.get().getKothwebhookConfig().reload();
    }

    public HCFCoreCommand(Main plugin) {
        plugin.getCommand("hcfcore").setExecutor(this);
    }

    private void getUsage(CommandSender sender, String label) {
        sender.sendMessage(CC.translate("&7&m-----------------------------"));
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate(String.valueOf((new StringBuilder()).append(" &6/").append(label).append(" reload"))));
        sender.sendMessage(CC.translate(String.valueOf((new StringBuilder()).append(" &6/").append(label).append(" help"))));
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate("&7&m-----------------------------"));
    }

    private void getInfo(CommandSender sender) {
        sender.sendMessage(CC.translate("&7&m-----------------------------"));
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate(" &3Author&7: &bDanidev819"));
        sender.sendMessage(CC.translate(" &3Version&7: &b" + plugin.getDescription().getVersion()));
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate("&7&m-----------------------------"));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("fhcf.command.admin")) {
            sender.sendMessage(CC.translate("&cNo permissions."));
            return true;
        } else if (args.length < 1) {
            this.getUsage(sender, label);
            return true;
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("fhcf.help")) {
                    sender.sendMessage(CC.translate("&cNo permissions."));
                    return true;
                }

                this.getInfo(sender);
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("fhcf.reload")) {
                    sender.sendMessage(CC.translate("fhcf.reload"));
                    return true;
                }

                this.reloadFiles();
                sender.sendMessage(CC.translate("&3Files successfully reloaded!"));
            } else {
                this.getUsage(sender, label);
            }

            return true;
        }
    }
}
