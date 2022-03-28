package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeysCommand implements CommandExecutor {

    private final FileConfig mainConfig = Main.get().getMainConfig();

    public KeysCommand(Main plugin) {
        plugin.getCommand("keys").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());

        if (!factionAt.isSafezone()) {
            player.sendMessage(CC.translate("&cYou need to be at the spawn to claim your keys"));
            return true;
        }

        FactionUser factionUser = Main.get().getUserManager().getUser(player.getUniqueId());
        int keys = factionUser.getKeys();

        if (keys <= 0) {
            player.sendMessage(CC.translate("&cYou have no key to claim"));
            return true;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), mainConfig.getString("KEY_REWARD")
                .replace("%PLAYER%", player.getName())
                .replace("%AMOUNT%", String.valueOf(keys)));
        factionUser.setKeys(0);
        player.sendMessage(CC.translate("&aYou have claimed x" + keys + " keys"));
        return true;
    }
}
