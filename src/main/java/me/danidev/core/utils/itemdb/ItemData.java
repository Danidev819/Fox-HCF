package me.danidev.core.utils.itemdb;

import me.danidev.core.Main;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemData implements ConfigurationSerializable {

    private Material material;
    private short itemData;

    @SuppressWarnings("deprecation")
    public ItemData(MaterialData data) {
        this(data.getItemType(), data.getData());
    }

    @SuppressWarnings("deprecation")
    public ItemData(ItemStack stack) {
        this(stack.getType(), stack.getData().getData());
    }

    public ItemData(Material material, short itemData) {
        this.material = material;
        this.itemData = itemData;
    }

    public ItemData(Map<String, Object> map) {
        Object object = map.get("itemType");
        if (!(object instanceof String)) {
            throw new AssertionError("Incorrectly configurised");
        }
        this.material = Material.getMaterial((String) object);
        if ((object = map.get("itemData")) instanceof Short) {
            this.itemData = (short) object;
            return;
        }
        throw new AssertionError("Incorrectly configurised");
    }

    @SuppressWarnings("deprecation")
    public static ItemData fromItemName(String string) {
        ItemStack stack = Main.get().getItemDb().getItem(string);
        return new ItemData(stack.getType(), stack.getData().getData());
    }

    public static ItemData fromStringValue(String value) {
        int firstBracketIndex = value.indexOf(40);
        if (firstBracketIndex == -1) {
            return null;
        }
        int otherBracketIndex = value.indexOf(41);
        if (otherBracketIndex == -1) {
            return null;
        }
        String itemName = value.substring(0, firstBracketIndex);
        String itemData = value.substring(firstBracketIndex + 1, otherBracketIndex);
        Material material = Material.getMaterial(itemName);
        return new ItemData(material, Short.parseShort(itemData));
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("itemType", this.material.name());
        map.put("itemData", this.itemData);
        return map;
    }

    public Material getMaterial() {
        return this.material;
    }

    @Deprecated
    public short getItemData() {
        return this.itemData;
    }

    public String getItemName() {
        return Main.get().getItemDb().getName(new ItemStack(this.material, 1, this.itemData));
    }

    @Override
    public String toString() {
        return this.material.name() + "(" + String.valueOf(this.itemData) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ItemData itemData1 = (ItemData) o;
        return this.itemData == itemData1.itemData && this.material == itemData1.material;
    }

    @Override
    public int hashCode() {
        int result = (this.material != null) ? this.material.hashCode() : 0;
        result = 31 * result + this.itemData;
        return result;
    }
}
