package me.danidev.core.commands.chat.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.file.FileConfig;
import me.danidev.core.handlers.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ChatSlowArgument extends CommandArgument {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public ChatSlowArgument() {
        super("slow", "Chat Slow");
        this.permission = "fhcf.command.chat.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CC.translate("&cUsage: /" + label + " <time>"));
            return true;
        }

        Integer time = JavaUtils.tryParseInt(args[1]);

        if (time == null) {
            sender.sendMessage(CC.translate("&c'" + args[1] + "' is not a valid number."));
            return true;
        }
        if (time <= 0) {
            Bukkit.broadcastMessage(CC.translate("&eChat delay has been set to default."));
            return true;
        }
        ChatHandler.setChatDelay(time);
        Bukkit.broadcastMessage(CC.translate(langConfig.getString("CHAT.SLOW")
                .replace("%PLAYER%", sender.getName())
                .replace("%TIME%", time.toString())));
        return true;
    }
}
