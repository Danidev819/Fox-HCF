package me.danidev.core.managers.blockshop;

import me.danidev.core.Main;
import me.danidev.core.managers.blockshop.block.BlockShopBlock;
import me.danidev.core.managers.blockshop.category.BlockShopCategory;
import me.danidev.core.utils.file.FileConfig;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class BlockShopManager {

    private final List<BlockShopCategory> categories = Lists.newArrayList();
    private final List<BlockShopBlock> blocks = Lists.newArrayList();

    private final FileConfig blockShopConfig = Main.get().getBlockShopConfig();

    public void loadCategories() {
        this.categories.clear();

        Set<String> section = blockShopConfig.getConfiguration().getConfigurationSection("BLOCKSHOP.CATEGORY").getKeys(false);

        section.forEach(category -> {
            String displayName = blockShopConfig.getString("BLOCKSHOP.CATEGORY." + category + ".ICON.DISPLAYNAME");
            List<String> description = blockShopConfig.getStringList("BLOCKSHOP.CATEGORY." + category + ".ICON.DESCRIPTION");
            int material = blockShopConfig.getInt("BLOCKSHOP.CATEGORY." + category + ".ICON.MATERIAL");
            int data = blockShopConfig.getInt("BLOCKSHOP.CATEGORY." + category + ".ICON.DATA");
            int slot = blockShopConfig.getInt("BLOCKSHOP.CATEGORY." + category + ".ICON.SLOT");
            boolean isSubMenu = blockShopConfig.getBoolean("BLOCKSHOP.CATEGORY." + category + ".SUB_MENU");

            this.categories.add(new BlockShopCategory(category, displayName, description, material, data, slot, isSubMenu));
        });
    }

    public void loadBlocks() {
        this.blocks.clear();

        Set<String> sectionBlockShop = blockShopConfig.getConfiguration().getConfigurationSection("BLOCKSHOP.CATEGORY").getKeys(false);

        sectionBlockShop.forEach(category -> {
            if (blockShopConfig.getBoolean("BLOCKSHOP.CATEGORY." + category + ".SUB_MENU")) {

                Set<String> sectionBlocks = blockShopConfig.getConfiguration().getConfigurationSection("BLOCKS.CATEGORY." + category).getKeys(false);

                sectionBlocks.forEach(block -> {
                    String displayName = blockShopConfig.getString("BLOCKS.CATEGORY." + category + "." + block + ".ICON.DISPLAYNAME");
                    List<String> description = blockShopConfig.getStringList("BLOCKS.CATEGORY." + category + "." + block + ".ICON.DESCRIPTION");
                    int material = blockShopConfig.getInt("BLOCKS.CATEGORY." + category + "." + block + ".ICON.MATERIAL");
                    int data = blockShopConfig.getInt("BLOCKS.CATEGORY." + category + "." + block + ".ICON.DATA");
                    int amount = blockShopConfig.getInt("BLOCKS.CATEGORY." + category + "." + block + ".ICON.AMOUNT");
                    int slot = blockShopConfig.getInt("BLOCKS.CATEGORY." + category + "." + block + ".ICON.SLOT");
                    int price = blockShopConfig.getInt("BLOCKS.CATEGORY." + category + "." + block + ".PRICE");

                    this.blocks.add(new BlockShopBlock(category, block, displayName, description, material, data, amount, slot, price));
                });
            }
        });
    }
}
