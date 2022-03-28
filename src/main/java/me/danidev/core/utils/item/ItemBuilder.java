package me.danidev.core.utils.item;

import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemStack stack;
    private ItemMeta meta;
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material, 1);
    }

    public ItemBuilder(int material) {
        this.itemStack = new ItemStack(material, 1);
    }

    public ItemBuilder displayName(String name) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder(String material) {
        this.itemStack = new ItemStack(Material.valueOf(material), 1);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder(Material material, int damage) {
        this.itemStack = new ItemStack(material, 1, (short) damage);
    }

    public ItemBuilder(Material material, int amount, int damage) {
        this.itemStack = new ItemStack(material, amount, (short) damage);
    }

    public ItemBuilder name(String name) {
        if (name != null) {
            name = CC.translate(name);
            ItemMeta meta = this.itemStack.getItemMeta();
            meta.setDisplayName(name);
            this.itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (lore != null) {
            ItemMeta meta = this.itemStack.getItemMeta();
            meta.setLore(CC.translate(lore));
            this.itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (lore != null) {
            ItemMeta meta = this.itemStack.getItemMeta();
            meta.setLore(CC.translate(Arrays.asList(lore)));
            this.itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder enchant(final Enchantment enchantment, final int level) {
        return this.enchant(enchantment, level, true);
    }

    public ItemBuilder enchant(final Enchantment enchantment, final int level, final boolean unsafe) {
        if (unsafe && level >= enchantment.getMaxLevel()) {
            this.stack.addUnsafeEnchantment(enchantment, level);
        } else {
            this.stack.addEnchantment(enchantment, level);
        }
        return this;
    }
    public ItemBuilder data(int dur) {
        this.itemStack.setDurability((short) dur);
        return this;
    }

    public ItemBuilder owner(String owner) {
        if (this.itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();
            meta.setOwner(owner);
            this.itemStack.setItemMeta(meta);
            return this;
        }

        throw new IllegalArgumentException("setOwner() only applicable for Skull Item");
    }

    public ItemStack toItemStack() {
        return this.stack;
    }

    public ItemBuilder armorColor(Color color) {
        try {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemStack.getItemMeta();
            leatherArmorMeta.setColor(color);
            this.itemStack.setItemMeta(leatherArmorMeta);
        }
        catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage("Error set armor color.");
        }
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            ItemMeta meta = this.itemStack.getItemMeta();

            meta.addEnchant(new Glow(), 1, true);
            this.itemStack.setItemMeta(meta);
        }
        return this;
    }

    public static void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glow glow = new Glow();
            Enchantment.registerEnchantment(glow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Glow extends Enchantment {

        public Glow() {
            super(25);
        }

        @Override
        public boolean canEnchantItem(ItemStack arg0) {
            return false;
        }

        @Override
        public boolean conflictsWith(Enchantment arg0) {
            return false;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return null;
        }

        @Override
        public int getMaxLevel() {
            return 2;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int getStartLevel() {
            return 1;
        }
    }

    public ItemStack build() {
        return this.itemStack;
    }
}