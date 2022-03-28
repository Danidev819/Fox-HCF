package me.danidev.core.managers.kit;

import me.danidev.core.Main;
import me.danidev.core.managers.kit.event.KitApplyEvent;
import me.danidev.core.utils.others.GenericUtils;
import me.danidev.core.utils.others.InventoryUtils;
import com.google.common.base.Preconditions;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class Kit implements ConfigurationSerializable {

    private static final ItemStack DEFAULT_IMAGE;

    static {
        DEFAULT_IMAGE = new ItemStack(Material.EMERALD, 1);
    }

    protected UUID uniqueID;
    protected String name;
    protected String description;
    protected ItemStack[] items;
    protected ItemStack[] armour;
    protected Collection<PotionEffect> effects;
    protected ItemStack image;
    protected boolean enabled;
    protected long delayMillis;
    protected String delayWords;
    protected long minPlaytimeMillis;
    protected String minPlaytimeWords;
    protected int maximumUses;
    protected int slot;

    public Kit(String name, String description, PlayerInventory inventory, Collection<PotionEffect> effects) {
        this(name, description, inventory, effects, 0L);
    }

    public Kit(String name, String description, Inventory inventory, Collection<PotionEffect> effects, long milliseconds) {
        this.enabled = true;
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.setItems(inventory.getContents());
        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory) inventory;
            this.setArmour(playerInventory.getArmorContents());
            this.setImage(playerInventory.getItemInHand());
        }
        this.effects = effects;
        this.delayMillis = milliseconds;
        this.maximumUses = Integer.MAX_VALUE;
        this.slot = 1;
    }

    public Kit(Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.setName((String) map.get("name"));
        this.setDescription((String) map.get("description"));
        this.setEnabled((boolean) map.get("enabled"));
        this.setEffects(GenericUtils.createList(map.get("effects"), PotionEffect.class));
        List<ItemStack> items = GenericUtils.createList(map.get("items"), ItemStack.class);
        this.setItems(items.toArray(new ItemStack[items.size()]));
        List<ItemStack> armour = GenericUtils.createList(map.get("armour"), ItemStack.class);
        this.setArmour(armour.toArray(new ItemStack[armour.size()]));
        this.setImage((ItemStack) map.get("image"));
        this.setDelayMillis(Long.parseLong((String) map.get("delay")));
        this.setMaximumUses((int) map.get("maxUses"));
        this.setSlot((int) map.get("slot"));
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("enabled", this.enabled);
        map.put("effects", this.effects);
        map.put("items", this.items);
        map.put("armour", this.armour);
        map.put("image", this.image);
        map.put("delay", Long.toString(this.delayMillis));
        map.put("maxUses", this.maximumUses);
        map.put("slot", this.slot);
        return map;
    }

    public Inventory getPreview(Player player) {
        Inventory inventory = Bukkit.createInventory(player, InventoryUtils.getSafestInventorySize(this.items.length), ChatColor.GREEN + this.name + " Preview");
        ItemStack[] items;
        for (int length = (items = this.items).length, i = 0; i < length; ++i) {
            ItemStack itemStack = items[i];
            inventory.addItem(itemStack);
        }
        return inventory;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemStack[] getItems() {
        return Arrays.copyOf(this.items, this.items.length);
    }

    public void setItems(ItemStack[] items) {
        int length = items.length;
        this.items = new ItemStack[length];
        for (int i = 0; i < length; ++i) {
            ItemStack next = items[i];
            this.items[i] = ((next == null) ? null : next.clone());
        }
    }

    public ItemStack[] getArmour() {
        return Arrays.copyOf(this.armour, this.armour.length);
    }

    public void setArmour(ItemStack[] armour) {
        int length = armour.length;
        this.armour = new ItemStack[length];
        for (int i = 0; i < length; ++i) {
            ItemStack next = armour[i];
            this.armour[i] = ((next == null) ? null : next.clone());
        }
    }

    public ItemStack getImage() {
        if (this.image == null || this.image.getType() == Material.AIR) {
            this.image = Kit.DEFAULT_IMAGE;
        }
        return this.image;
    }

    public void setImage(ItemStack image) {
        this.image = ((image == null || image.getType() == Material.AIR) ? null : image.clone());
    }

    public Collection<PotionEffect> getEffects() {
        return this.effects;
    }

    public void setEffects(Collection<PotionEffect> effects) {
        this.effects = effects;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getDelayMillis() {
        return this.delayMillis;
    }

    public void setDelayMillis(long delayMillis) {
        if (this.delayMillis != delayMillis) {
            Preconditions.checkArgument(this.minPlaytimeMillis >= 0L, "Minimum delay millis cannot be negative");
            this.delayMillis = delayMillis;
            this.delayWords = DurationFormatUtils.formatDurationWords(delayMillis, true, true);
        }
    }

    public String getDelayWords() {
        return DurationFormatUtils.formatDurationWords(this.delayMillis, true, true);
    }

    public long getMinPlaytimeMillis() {
        return this.minPlaytimeMillis;
    }

    public void setMinPlaytimeMillis(long minPlaytimeMillis) {
        if (this.minPlaytimeMillis != minPlaytimeMillis) {
            Preconditions.checkArgument(minPlaytimeMillis >= 0L, "Minimum playtime millis cannot be negative");
            this.minPlaytimeMillis = minPlaytimeMillis;
            this.minPlaytimeWords = DurationFormatUtils.formatDurationWords(minPlaytimeMillis, true, true);
        }
    }

    public String getMinPlaytimeWords() {
        return this.minPlaytimeWords;
    }

    public int getMaximumUses() {
        return this.maximumUses;
    }

    public void setMaximumUses(int maximumUses) {
        Preconditions.checkArgument(maximumUses >= 0, "Maximum uses cannot be negative");
        this.maximumUses = maximumUses;
    }

    public int getSlot() {
        return slot;
    }

    public int setSlot(int a) {
        return slot = a;
    }

    public String getPermissionNode() {
        return "fhcf.kit." + this.name;
    }

    public Permission getBukkitPermission() {
        String node = this.getPermissionNode();
        return (node == null) ? null : new Permission(node);
    }

    public boolean applyTo(Player player, boolean force, boolean inform) {
        PlayerInventory inv = player.getInventory();
        KitApplyEvent event = new KitApplyEvent(this, player, force);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        if (Main.get().isKitMap()) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            inv.setContents(new ItemStack[36]);
            inv.setArmorContents(new ItemStack[4]);
        }
        ItemStack cursor = player.getItemOnCursor();
        Location location = player.getLocation();
        World world = player.getWorld();
        if (cursor != null && cursor.getType() != Material.AIR) {
            player.setItemOnCursor(new ItemStack(Material.AIR, 1));
            world.dropItemNaturally(location, cursor);
        }
        PlayerInventory inventory = player.getInventory();
        for (ItemStack item : this.items) {
            if (item != null) {
                if (item.getType() != Material.AIR) {
                    item = item.clone();
                    for (Map.Entry excess : inventory.addItem(new ItemStack[]{item.clone()}).entrySet()) {
                        world.dropItemNaturally(location, (ItemStack) excess.getValue());
                    }
                }
            }
        }
        if (this.armour != null) {
            for (int i = Math.min(3, this.armour.length); i >= 0; --i) {
                ItemStack stack = this.armour[i];
                if (stack != null) {
                    if (stack.getType() != Material.AIR) {
                        int armourSlot = i + 36;
                        ItemStack previous = inventory.getItem(armourSlot);
                        stack = stack.clone();
                        if (previous != null && previous.getType() != Material.AIR) {
                            previous.setType(Material.AIR);
                            world.dropItemNaturally(location, stack);
                        } else {
                            inventory.setItem(armourSlot, stack);
                        }
                    }
                }
            }
        }
        if (inform) {
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.AQUA + this.name + ChatColor.YELLOW + " has been applied.");
        }
        player.updateInventory();
        return true;
    }
}
