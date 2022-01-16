package com.ddf.fakeplayer.container.inventory;

import com.ddf.fakeplayer.item.ItemStack;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryAction action = (InventoryAction) o;
        return mSlot == action.mSlot && Objects.equals(mSource, action.mSource) && Objects.equals(mFromItem, action.mFromItem) && Objects.equals(mToItem, action.mToItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSource, mSlot, mFromItem, mToItem);
    }
}
