package me.danidev.core.managers.kit;

import me.danidev.core.Main;
import me.danidev.core.managers.timer.event.TimerStartEvent;
import me.danidev.core.managers.timer.type.ProtectionTimer;
import me.danidev.core.managers.user.BaseUser;
import me.danidev.core.managers.kit.event.KitApplyEvent;
import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitListener implements Listener {

    public static List<Inventory> PREVIEW = Lists.newArrayList();

    public KitListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        PREVIEW.remove(e.getInventory());
    }
    
    @EventHandler
	public void onTimer(TimerStartEvent e) {
		if (Main.get().isKitMap() && e.getTimer() instanceof ProtectionTimer) {
			Main.get().getTimerManager().protectionTimer.clearCooldown(e.getUserUUID().get());
		}
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (PREVIEW.contains(event.getInventory())) {
            event.setCancelled(true);
            return;
        }
        Inventory inventory = event.getInventory();

        if (inventory == null) {
            return;
        }

        String title = inventory.getTitle();
        HumanEntity humanEntity = event.getWhoClicked();

        if (title.contains("Kit Selector") && humanEntity instanceof Player) {
            event.setCancelled(true);

            if (!Objects.equals(event.getView().getTopInventory(), event.getClickedInventory())) return;

            ItemStack stack = event.getCurrentItem();

            if (stack == null || !stack.hasItemMeta()) return;

            ItemMeta meta = stack.getItemMeta();

            if (!meta.hasDisplayName()) return;

            Player player = (Player) humanEntity;
            String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            Kit kit = Main.get().getKitManager().getKit(name);

            if (kit == null) return;

            kit.applyTo(player, false, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitSign(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();

            if (!(state instanceof Sign)) return;

            Player player = event.getPlayer();
            Sign sign = (Sign) state;
            String[] lines = sign.getLines();

            if (lines.length >= 2 && lines[1].contains("Kit")) {
                Kit kit = Main.get().getKitManager().getKit((lines.length >= 3) ? lines[2] : null);

                if (kit == null) return;

                event.setCancelled(true);

                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                boolean applied = kit.applyTo(player, false, false);

                if (applied) {
                    fakeLines[0] = ChatColor.GREEN + "Successfully";
                    fakeLines[1] = ChatColor.GREEN + "equipped kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = "";
                }
                else {
                    fakeLines[0] = ChatColor.RED + "Failed to";
                    fakeLines[1] = ChatColor.RED + "equip kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = ChatColor.RED + "Check chat";
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onKitApply(KitApplyEvent event) {
        if (event.isForce()) return;

        Player player = event.getPlayer();
        Kit kit = event.getKit();

        if (!player.isOp() && !kit.isEnabled()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "The " + kit.getDisplayName() + " kit is currently disabled.");
            return;
        }

        String kitPermission = kit.getPermissionNode();

        if (kitPermission != null && !player.hasPermission(kitPermission)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not have permission to use this kit.");
            return;
        }

        UUID uuid = player.getUniqueId();
        long minPlaytimeMillis = kit.getMinPlaytimeMillis();

        if (minPlaytimeMillis > 0L && Main.get().getPlayTimeManager().getTotalPlayTime(uuid) < minPlaytimeMillis) {
            player.sendMessage(ChatColor.RED + "You need at least " + kit.getMinPlaytimeWords() + " minimum playtime to use kit " + kit.getDisplayName() + '.');
            event.setCancelled(true);
            return;
        }

        BaseUser baseUser = Main.get().getUserManager().getBaseUser(uuid);
        long remaining = baseUser.getRemainingKitCooldown(kit);

        if (remaining > 0L) {
            player.sendMessage(ChatColor.RED + "You cannot use the " + kit.getDisplayName() + " kit for " + DurationFormatUtils.formatDurationWords(remaining, true, true) + '.');
            event.setCancelled(true);
            return;
        }

        int curUses = baseUser.getKitUses(kit);
        int maxUses;

        if (curUses >= (maxUses = kit.getMaximumUses()) && maxUses != Integer.MAX_VALUE) {
            player.sendMessage(ChatColor.RED + "You have already used this kit " + curUses + '/' + maxUses + " times.");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitApplyMonitor(KitApplyEvent event) {
        if (!event.isForce()) {
            Kit kit = event.getKit();
            BaseUser baseUser = Main.get().getUserManager().getBaseUser(event.getPlayer().getUniqueId());
            baseUser.incrementKitUses(kit);
            baseUser.updateKitCooldown(kit);
        }
    }
}
