package me.danidev.core.managers.kit.argument;

import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.Main;

import java.util.Collections;
import java.util.List;

public class KitHelpArgument extends CommandArgument {

    public KitHelpArgument(Main plugin) {
        super("help", "Kit help");
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate("&6&lKit Help"));
        sender.sendMessage(CC.translate("&e/" + label + " apply <kitName> <player>"));
        sender.sendMessage(CC.translate("&e/" + label + " create <kitName> [description]"));
        sender.sendMessage(CC.translate("&e/" + label + " delete <kitName>"));
        sender.sendMessage(CC.translate("&e/" + label + " setdescription <kitName> <none:description>"));
        sender.sendMessage(CC.translate("&e/" + label + " disable <kitName>"));
        sender.sendMessage(CC.translate("&e/" + label + " gui"));
        sender.sendMessage(CC.translate("&e/" + label + " list"));
        sender.sendMessage(CC.translate("&e/" + label + " preview <kitName>"));
        sender.sendMessage(CC.translate("&e/" + label + " rename <kitName> <newKitName>"));
        sender.sendMessage(CC.translate("&e/" + label + " setdelay <kitName> <delay>"));
        sender.sendMessage(CC.translate("&e/" + label + " setimage <kitName>"));
        sender.sendMessage(CC.translate("&e/" + label + " setitems <kitName>"));
        sender.sendMessage(CC.translate("&e/" + label + " setmaxuses <kitName> <amount:unlimited>"));
        sender.sendMessage(CC.translate("&e/" + label + " setminplaytime <kitName> <time>"));
        sender.sendMessage(CC.translate("&e/" + label + " setslot <kitName> <slot>"));
        sender.sendMessage(CC.translate(""));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
