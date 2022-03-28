package me.danidev.core.managers.blockshop.block;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class BlockShopBlock {

    private final String category;
    private final String block;
    private final String displayName;
    private final List<String> description;
    private final int icon;
    private final int data;
    private final int amount;
    private final int slot;
    private final int price;

    public List<String> getDescription() {
        return this.description.stream()
                .map(map -> map.replace("%PRICE%", String.valueOf(this.getPrice())))
                .collect(Collectors.toList());
    }
}
