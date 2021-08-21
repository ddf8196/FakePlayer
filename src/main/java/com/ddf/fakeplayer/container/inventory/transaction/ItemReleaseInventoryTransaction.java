package com.ddf.fakeplayer.container.inventory.transaction;

import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.Vec3;

public class ItemReleaseInventoryTransaction extends ComplexInventoryTransaction {
    private ItemReleaseInventoryTransaction.ActionType mActionType;
    private int mSlot;
    private ItemStack mItem;
    private Vec3 mFromPos;

    public ItemReleaseInventoryTransaction(ItemRegistry itemRegistry) {
        super(itemRegistry, Type.ItemReleaseTransaction);
    }

    public ItemReleaseInventoryTransaction(final InventoryTransaction transaction) {
        super(Type.ItemReleaseTransaction, transaction);
        this.mActionType = ActionType.Release_0;
        this.mSlot = -1;
        this.mItem = new ItemStack(transaction.itemRegistry);
        this.mFromPos = new Vec3();
    }

    public final ItemStack getSelectedItem() {
        return this.mItem;
    }

    public final int getSelectedSlot() {
        return this.mSlot;
    }

    public final Vec3 getFromPosition() {
        return this.mFromPos;
    }

    public final ItemReleaseInventoryTransaction.ActionType getActionType() {
        return this.mActionType;
    }

    public final ItemReleaseInventoryTransaction setSelectedItem(final ItemStack item) {
        this.mItem = item;
        return this;
    }

    public final ItemReleaseInventoryTransaction setSelectedSlot(int slot) {
        this.mSlot = slot;
        return this;
    }

    public final ItemReleaseInventoryTransaction setFromPosition(final Vec3 pos) {
        this.mFromPos = pos;
        return this;
    }

    public final ItemReleaseInventoryTransaction setActionType(ItemReleaseInventoryTransaction.ActionType type) {
        this.mActionType = type;
        return this;
    }

    public enum ActionType {
        Release_0,
        Use_0
    }
}
