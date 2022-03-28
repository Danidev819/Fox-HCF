package me.danidev.core.managers.wrench;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Wrench {

    public static int MAX_END_DRAGON_USES = 1;
    public static Material WRENCH_TYPE;
    private static final String WRENCH_NAME;
    private static final String END_DRAGON_USE_TAG = "End Dragon Uses";
    private static final String SPAWNER_USE_TAG = "Spawner Uses";
    private static final String END_FRAME_USE_TAG = "End Frame Uses";
    private static final String LORE_FORMAT;
    private final ItemStack stack;
    private int endFrameUses;
    private int endDragonUses;
    private int spawnerUses;
    private boolean needsMetaUpdate;

    static {
        WRENCH_TYPE = Material.GOLD_PICKAXE;
        WRENCH_NAME = String.valueOf(ChatColor.RED.toString()) + ChatColor.ITALIC + "Wrench";
        LORE_FORMAT = ChatColor.GRAY + "%1$s: " + ChatColor.YELLOW + "%2$s/%3$s";
    }

    public Wrench() {
        this(1, 6, 1);
    }

    public Wrench(int spawnerUses, int endFrameUses, int endDragonUses) {
        this.stack = new ItemStack(Wrench.WRENCH_TYPE, 1);
        Preconditions.checkArgument(spawnerUses > 0 || endFrameUses > 0, "Cannot create a wrench with empty uses");
        this.setSpawnerUses(Math.min(1, spawnerUses));
        this.setEndDragonUses(Math.min(1, endDragonUses));
        this.setEndFrameUses(Math.min(6, endFrameUses));
    }

    public static Optional<Wrench> fromStack(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return Optional.absent();
        }

        ItemMeta meta = stack.getItemMeta();

        if (!meta.hasDisplayName() || !meta.hasLore() || !meta.getDisplayName().equals(Wrench.WRENCH_NAME)) {
            return Optional.absent();
        }

        Wrench wrench = new Wrench();
        List<String> loreList = meta.getLore();

        for (String lore : loreList) {
            lore = ChatColor.stripColor(lore);
            for (int length = lore.length(), i = 0; i < length; ++i) {
                char character = lore.charAt(i);
                if (Character.isDigit(character)) {
                    int amount = Integer.parseInt(String.valueOf(character));
                    if (lore.startsWith(SPAWNER_USE_TAG)) {
                        wrench.setSpawnerUses(amount);
                        break;
                    }
                    if (lore.startsWith(END_FRAME_USE_TAG)) {
                        wrench.setEndFrameUses(amount);
                        break;
                    }
                    if (lore.startsWith(END_DRAGON_USE_TAG)) {
                        wrench.setEndDragonUses(amount);
                        break;
                    }
                }
            }
        }
        return Optional.of(wrench);
    }

    public int getEndDragonUses() {
        return this.endDragonUses;
    }

    public void setEndDragonUses(int uses) {
        if (this.endDragonUses != uses) {
            this.endDragonUses = Math.min(1, uses);
            this.needsMetaUpdate = true;
        }
    }

    public int getEndFrameUses() {
        return this.endFrameUses;
    }

    public void setEndFrameUses(int uses) {
        if (this.endFrameUses != uses) {
            this.endFrameUses = Math.min(6, uses);
            this.needsMetaUpdate = true;
        }
    }

    public int getSpawnerUses() {
        return this.spawnerUses;
    }

    public void setSpawnerUses(int uses) {
        if (this.spawnerUses != uses) {
            this.spawnerUses = Math.min(1, uses);
            this.needsMetaUpdate = true;
        }
    }

    public ItemStack getItemIfPresent() {
        Optional<ItemStack> optional = this.toItemStack();
        return (optional.isPresent() ? optional.get() : new ItemStack(Material.AIR, 1));
    }

    public Optional<ItemStack> toItemStack() {
        if (this.needsMetaUpdate) {
            double curDurability;
            double maxDurability = curDurability = Wrench.WRENCH_TYPE.getMaxDurability();
            double increment = curDurability / 6.0;
            curDurability -= increment * (this.spawnerUses + this.endFrameUses);
            if (Math.abs(curDurability - maxDurability) == 0.0) {
                return Optional.absent();
            }
            ItemMeta meta = this.stack.getItemMeta();
            meta.setDisplayName(Wrench.WRENCH_NAME);
            meta.setLore(Arrays.asList(
                    String.format(Wrench.LORE_FORMAT, "Spawner Uses", this.spawnerUses, 1),
                    String.format(Wrench.LORE_FORMAT, "End Frame Uses", this.endFrameUses, 6)
            ));
            this.stack.setItemMeta(meta);
            this.stack.setDurability((short) curDurability);
            this.needsMetaUpdate = false;
        }
        return Optional.of(this.stack);
    }
}
