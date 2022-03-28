package me.danidev.core.managers.blockshop.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BlockShopCategory {

    private final String category;
    private final String displayName;
    private final List<String> description;
    private final int material;
    private final int data;
    private final int slot;
    private final boolean subMenu;
}
