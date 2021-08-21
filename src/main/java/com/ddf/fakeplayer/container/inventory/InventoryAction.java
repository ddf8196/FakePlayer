package com.ddf.fakeplayer.container.inventory;

import com.ddf.fakeplayer.item.ItemStack;

public class InventoryAction {
    private final InventorySource mSource;
    private final /*uint32_t*/int mSlot;
    private final ItemStack mFromItem;
    private final ItemStack mToItem;

    public InventoryAction(InventorySource source, int slot, final ItemStack fromItem, final ItemStack toItem) {
        this.mSource = source;
        this.mSlot = slot;
        this.mFromItem = new ItemStack(fromItem);
        this.mToItem = new ItemStack(toItem);
    }

    public final InventorySource getSource() {
        return this.mSource;
    }

    public final int getSlot() {
        return this.mSlot;
    }

    public final ItemStack getFromItem() {
        return this.mFromItem;
    }

    public final ItemStack getToItem() {
        return this.mToItem;
    }
}
