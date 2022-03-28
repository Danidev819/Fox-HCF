package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();
	
    public PingCommand(Main plugin) {
        plugin.getCommand("ping").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(CC.translate(langConfig.getString("PING.YOUR_SELF")
                    .replace("%PING%", String.valueOf(getPing(player)))));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        player.sendMessage(CC.translate(langConfig.getString("PING.OTHER")
                .replace("%PLAYER%", target.getName())
                .replace("%PING%", String.valueOf(getPing(target)))));
        return true;
    }

    private int getPing(Player player) {
        try {
            String a = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> b = Class.forName("org.bukkit.craftbukkit." + a + ".entity.CraftPlayer");
            Object c = b.getMethod("getHandle").invoke(player);
            return (int) c.getClass().getDeclaredField("ping").get(c);
        }
        catch (Exception e) {
            return 0;
        }
    }
}
