package com.ddf.fakeplayer.container.inventory.transaction;

import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.nbt.CompoundTag;

public class InventoryTransactionItemGroup {
    private ItemRegistry itemRegistry;
    private int mItemId;
    private int mItemAux;
    private CompoundTag mTag;
    private int mCount;
    private boolean mOverflow;

    public InventoryTransactionItemGroup(ItemRegistry itemRegistry, final ItemStack item, int count) {
        this.itemRegistry = itemRegistry;
        this.mItemId = item.getId();
        this.mItemAux = item.getAuxValue();
        this.mCount = count;
        this.mOverflow = false;
        CompoundTag UserData = item.getUserData();
        if (UserData != null) {
            this.mTag = UserData.clone();
        }
    }

    public final boolean _itemAuxMatch(final int auxValue) {
        if (auxValue == 0x7FFF || this.mItemAux == 0x7FFF)
            return true;
        else
            return this.mItemAux == auxValue;
    }

    public final boolean add(final ItemStack item, int count) {
        if (this.equals(item)) {
            if ((count <= 0 || (this.mCount <= Integer.MAX_VALUE - count)) && (count >= 0 || (this.mCount >= Integer.MIN_VALUE - count))) {
                this.mCount += count;
            } else {
                this.mOverflow = true;
            }
            return true;
        }
        return false;
    }

    public final boolean equals(final ItemStack item) {
        if (item.getId() == this.mItemId && (!item.isStackedByData() || (this._itemAuxMatch(item.getAuxValue())))) {
            if (this.mTag != null) {
                if (item.getUserData() != null) {
                    return this.mTag.equals(item.getUserData());
                }
                return false;
            } else {
                return item.getUserData() == null;
            }
        } else {
            return false;
        }
    }

    public final int getCount() {
        return this.mCount;
    }

    public final ItemStack getItemInstance() {
        Item item = itemRegistry.getItem(this.mItemId);
        if (item != null) {
            int auxValue = this.mItemAux;
            return new ItemStack(itemRegistry, item, 1, auxValue, this.mTag);
        }
        return new ItemStack(itemRegistry);
    }

    public final boolean hasOverflow() {
        return this.mOverflow;
    }
}
