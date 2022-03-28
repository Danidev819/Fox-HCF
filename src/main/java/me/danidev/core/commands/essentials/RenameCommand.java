package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public RenameCommand(Main plugin) {
        plugin.getCommand("rename").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(CC.translate("&cUsage: /" + label + " <name>"));
            return true;
        }

        ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cYou need hold any item."));
            return true;
        }

        String name = StringUtils.join(args, ' ', 0, args.length);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        itemStack.setItemMeta(meta);

        player.sendMessage(CC.translate(langConfig.getString("RENAME")
                .replace("%NAME%", name)));
        return true;
    }
}
