package me.danidev.core.commands.inventory;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearInvCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();
	
    public ClearInvCommand(Main plugin) {
    	plugin.getCommand("clearinv").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            armorContent.setType(Material.AIR);
        }

        player.getInventory().clear();
        player.sendMessage(CC.translate(langConfig.getString("CLEAR_INV")));
        return true;
    }
}
