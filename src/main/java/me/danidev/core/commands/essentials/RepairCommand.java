package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public RepairCommand(Main plugin) {
        plugin.getCommand("repair").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cYou need hold any item."));
            return true;
        }

        if (!itemCheck(itemStack)) {
            player.sendMessage(CC.translate("&cThis item cannot be repaired."));
            return true;
        }

        if (itemStack.getDurability() == 0) {
            player.sendMessage(CC.translate("&cThis item is already full repaired."));
            return true;
        }

        itemStack.setDurability((short) 0);
        player.sendMessage(CC.translate(langConfig.getString("REPAIR")));
        return true;
    }

    private boolean itemCheck(ItemStack item) {
        return item.getType().getId() == 256 || item.getType().getId() == 257 ||
                item.getType().getId() == 258 || item.getType().getId() == 259 ||
                item.getType().getId() == 261 || item.getType().getId() == 267 ||
                item.getType().getId() == 268 || item.getType().getId() == 269 ||
                item.getType().getId() == 270 || item.getType().getId() == 271 ||
                item.getType().getId() == 272 || item.getType().getId() == 273 ||
                item.getType().getId() == 274 || item.getType().getId() == 275 ||
                item.getType().getId() == 276 || item.getType().getId() == 277 ||
                item.getType().getId() == 278 || item.getType().getId() == 279 ||
                item.getType().getId() == 283 || item.getType().getId() == 284 ||
                item.getType().getId() == 285 || item.getType().getId() == 286 ||
                item.getType().getId() == 290 || item.getType().getId() == 291 ||
                item.getType().getId() == 292 || item.getType().getId() == 293 ||
                item.getType().getId() == 294 || item.getType().getId() == 298 ||
                item.getType().getId() == 299 || item.getType().getId() == 300 ||
                item.getType().getId() == 301 || item.getType().getId() == 302 ||
                item.getType().getId() == 303 || item.getType().getId() == 304 ||
                item.getType().getId() == 305 || item.getType().getId() == 306 ||
                item.getType().getId() == 307 || item.getType().getId() == 308 ||
                item.getType().getId() == 309 || item.getType().getId() == 310 ||
                item.getType().getId() == 311 || item.getType().getId() == 312 ||
                item.getType().getId() == 313 || item.getType().getId() == 314 ||
                item.getType().getId() == 315 || item.getType().getId() == 316 ||
                item.getType().getId() == 317 || item.getType().getId() == 346 ||
                item.getType().getId() == 359;
    }
}
