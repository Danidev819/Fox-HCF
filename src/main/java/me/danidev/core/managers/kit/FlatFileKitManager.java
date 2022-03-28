package me.danidev.core.managers.kit;

import me.danidev.core.managers.drops.CaseInsensitiveMap;
import me.danidev.core.managers.user.Config;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.others.GenericUtils;
import com.google.common.collect.Lists;
import me.danidev.core.Main;
import me.danidev.core.managers.kit.event.KitRenameEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;

import java.util.*;

public class FlatFileKitManager implements KitManager, Listener {

    private final Map<String, Kit> kitNameMap;
    private final Map<UUID, Kit> kitUUIDMap;
    private final Main plugin;
    private Config config;
    private List<Kit> kits;

    public FlatFileKitManager(Main plugin) {
        this.kitNameMap = new CaseInsensitiveMap<>();
        this.kitUUIDMap = new HashMap<>();
        this.kits = new ArrayList<>();
        this.plugin = plugin;
        this.reloadKitData();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitRename(KitRenameEvent event) {
        this.kitNameMap.remove(event.getOldName());
        this.kitNameMap.put(event.getNewName(), event.getKit());
    }

    @Override
    public List<Kit> getKits() {
        return this.kits;
    }

    @Override
    public Kit getKit(UUID uuid) {
        return this.kitUUIDMap.get(uuid);
    }

    @Override
    public Kit getKit(String id) {
        return this.kitNameMap.get(id);
    }

    @Override
    public boolean containsKit(Kit kit) {
        return this.kits.contains(kit);
    }

    @Override
    public void createKit(Kit kit) {
        if (this.kits.add(kit)) {
            this.kitNameMap.put(kit.getName(), kit);
            this.kitUUIDMap.put(kit.getUniqueID(), kit);
        }
    }

    @Override
    public void removeKit(Kit kit) {
        if (this.kits.remove(kit)) {
            this.kitNameMap.remove(kit.getName());
            this.kitUUIDMap.remove(kit.getUniqueID());
        }
    }

    @Override
    public Inventory getGui(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory(player, (Main.get().isKitMap() ? (this.kits.size() + 9 - 1) / 9 * 9 : 45), ChatColor.BLUE + "Kit Selector");
        for (Kit kit : this.kits) {
            ItemStack stack = kit.getImage();
            String description = kit.getDescription();
            String kitPermission = kit.getPermissionNode();
            List<String> lore;
            if (kitPermission == null || player.hasPermission(kitPermission)) {
                lore = new ArrayList<>();
                if (kit.isEnabled()) {
                    if (kit.getDelayMillis() > 0L) {
                        lore.add(ChatColor.YELLOW + kit.getDelayWords() + " cooldown");
                    }
                } else {
                    lore.add(ChatColor.RED + "Disabled");
                }
                int maxUses;
                if ((maxUses = kit.getMaximumUses()) != Integer.MAX_VALUE) {
                    lore.add(ChatColor.YELLOW + "Used " + this.plugin.getUserManager().getBaseUser(uuid).getKitUses(kit) + '/' + maxUses + " times.");
                }
                if (description != null) {
                    lore.add(" ");
                    for (String part : ChatPaginator.wordWrap(description, 24)) {
                        lore.add(ChatColor.WHITE + part);
                    }
                }
            } else {
                lore = Lists.newArrayList(ChatColor.RED + "You do not own this kit.");
            }
            ItemStack cloned = stack.clone();
            ItemMeta meta = cloned.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + kit.getName());
            meta.setLore(CC.translate(lore));
            cloned.setItemMeta(meta);
            if (Main.get().isKitMap()) {
                inventory.addItem(cloned);
            } 
            else {
                inventory.setItem(kit.getSlot() - 1, cloned);
            }
        }
        return inventory;
    }

    @Override
    public void reloadKitData() {
        this.config = new Config(this.plugin, "kits");
        Object object = this.config.get("kits");
        if (object instanceof List) {
            this.kits = GenericUtils.createList(object, Kit.class);
            for (Kit kit : this.kits) {
                this.kitNameMap.put(kit.getName(), kit);
                this.kitUUIDMap.put(kit.getUniqueID(), kit);
            }
        }
    }

    @Override
    public void saveKitData() {
        this.config.set("kits", this.kits);
        this.config.save();
    }
}
