package me.danidev.core.utils.others;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.Set;

public class InventoryUtils {

    public static ItemStack[] deepClone(ItemStack[] origin) {
        Preconditions.checkNotNull((Object) origin, "Origin cannot be null");
        ItemStack[] cloned = new ItemStack[origin.length];

        for (int i = 0; i < origin.length; ++i) {
            ItemStack next = origin[i];
            cloned[i] = ((next == null) ? null : next.clone());
        }

        return cloned;
    }

    public static int getSafestInventorySize(int initialSize) {
        return (initialSize + 8) / 9 * 9;
    }

    @SuppressWarnings("deprecation")
    public static void removeItem(Inventory inventory, Material type, short data, int quantity) {
        ItemStack[] contents = inventory.getContents();
        boolean compareDamage = type.getMaxDurability() == 0;

        for (int i = quantity; i > 0; --i) {
            int length = contents.length;
            int j = 0;

            while (j < length) {
                ItemStack content = contents[j];
                if (content != null && content.getType() == type
                        && (!compareDamage || content.getData().getData() == data)) {
                    if (content.getAmount() <= 1) {
                        inventory.removeItem(content);
                        break;
                    }
                    content.setAmount(content.getAmount() - 1);
                    break;
                } else {
                    ++j;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static int countAmount(Inventory inventory, Material type, short data) {
        ItemStack[] contents = inventory.getContents();
        boolean compareDamage = type.getMaxDurability() == 0;
        int counter = 0;
        ItemStack[] array;
        for (int length = (array = contents).length, i = 0; i < length; ++i) {
            ItemStack item = array[i];
            if (item != null && item.getType() == type && (!compareDamage || item.getData().getData() == data)) {
                counter += item.getAmount();
            }
        }
        return counter;
    }

    public static boolean isEmpty(Inventory inventory) {
        return isEmpty(inventory, true);
    }

    public static boolean isEmpty(Inventory inventory, boolean checkArmour) {
        boolean result = true;
        ItemStack[] array3 = inventory.getContents();
        ItemStack[] array5;
        for (int length = (array5 = array3).length, i = 0; i < length; ++i) {
            ItemStack content = array5[i];
            if (content != null && content.getType() != Material.AIR) {
                result = false;
                break;
            }
        }
        if (!result) {
            return false;
        }
        if (checkArmour && inventory instanceof PlayerInventory) {
            ItemStack[] array4 = ((PlayerInventory) inventory).getArmorContents();
            ItemStack[] array6;
            for (int length2 = (array6 = array4).length, j = 0; j < length2; ++j) {
                ItemStack content2 = array6[j];
                if (content2 != null && content2.getType() != Material.AIR) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public static boolean clickedTopInventory(InventoryDragEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        if (topInventory == null) {
            return false;
        }

        boolean result = false;
        Set<Map.Entry<Integer, ItemStack>> entrySet = event.getNewItems().entrySet();
        int size = topInventory.getSize();
        for (Map.Entry<Integer, ItemStack> entry : entrySet) {
            if (entry.getKey() < size) {
                result = true;
                break;
            }
        }
        return result;
    }
}
