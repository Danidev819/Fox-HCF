package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.wrench.Wrench;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.others.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class ShopSignListener implements Listener {

    private final Pattern ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
    
    public ShopSignListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();

            if (state instanceof Sign) {
                Sign sign = (Sign) state;
                String[] lines = sign.getLines();
                Integer quantity = JavaUtils.tryParseInt(lines[2]);

                if (quantity == null) return;

                Integer price = JavaUtils.tryParseInt(ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));

                if (price == null) return;

                ItemStack stack;

                if (lines[1].equalsIgnoreCase("Crowbar")) {
                    stack = new Wrench().getItemIfPresent();
                }
                else if ((stack = Main.get().getItemDb().getItem(ALPHANUMERIC_REMOVER.matcher(lines[1]).replaceAll(""), quantity)) == null) {
                    return;
                }

                Player player = event.getPlayer();
                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);

                if ((lines[0].contains("Sell") && lines[0].contains(ChatColor.RED.toString())) || lines[0].contains(ChatColor.AQUA.toString())) {
                    int sellQuantity = Math.min(quantity, InventoryUtils.countAmount(player.getInventory(), stack.getType(), stack.getDurability()));

                    if (sellQuantity <= 0) {
                        fakeLines[0] = ChatColor.RED + "Not carrying any";
                        fakeLines[2] = ChatColor.RED + "on you.";
                        fakeLines[3] = "";
                    }
                    else {
                        int newPrice = price / quantity * sellQuantity;
                        fakeLines[0] = ChatColor.GREEN + "Sold " + sellQuantity;
                        fakeLines[3] = ChatColor.GREEN + "for " + '$' + newPrice;
                        Main.get().getEconomyManager().addBalance(player.getUniqueId(), newPrice);
                        InventoryUtils.removeItem(player.getInventory(), stack.getType(), stack.getData().getData(), sellQuantity);
                        player.updateInventory();
                    }
                }
                else {
                    if (!lines[0].contains("Buy") || !lines[0].contains(ChatColor.GREEN.toString())) return;

                    if (price > Main.get().getEconomyManager().getBalance(player.getUniqueId())) {
                        fakeLines[0] = ChatColor.RED + "Cannot afford";
                    }
                    else {
                        fakeLines[0] = ChatColor.GREEN + "Item bought";
                        fakeLines[3] = ChatColor.GREEN + "for " + '$' + price;

                        Main.get().getEconomyManager().subtractBalance(player.getUniqueId(), price);

                        World world = player.getWorld();
                        Location location = player.getLocation();
                        Map<Integer, ItemStack> excess = player.getInventory().addItem(stack);

                        for (Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
                            world.dropItemNaturally(location, excessItemStack.getValue());
                        }

                        player.setItemInHand(player.getItemInHand());
                        player.updateInventory();
                    }
                }
                event.setCancelled(true);
                Main.get().getSignHandler().showLines(player, sign, fakeLines, 100L, true);
            }
        }
    }
}
