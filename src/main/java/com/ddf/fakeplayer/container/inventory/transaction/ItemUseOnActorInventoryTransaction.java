package com.ddf.fakeplayer.container.inventory.transaction;

import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.Vec3;

public class ItemUseOnActorInventoryTransaction extends ComplexInventoryTransaction {
    private /*ActorRuntimeID*/long mRuntimeId;
    private ItemUseOnActorInventoryTransaction.ActionType mActionType;
    private int mSlot;
    private ItemStack mItem;
    private Vec3 mFromPos;
    private Vec3 mHitPos;

    public ItemUseOnActorInventoryTransaction(ItemRegistry itemRegistry) {
        super(itemRegistry, Type.ItemUseOnEntityTransaction);
        this.mRuntimeId = 0L;
        this.mActionType = ActionType.Interact_1;
        this.mSlot = -1;
        this.mItem = new ItemStack(itemRegistry);
        this.mFromPos = new Vec3();
        this.mHitPos = new Vec3();
    }

    public ItemUseOnActorInventoryTransaction(final InventoryTransaction transaction) {
        super(Type.ItemUseOnEntityTransaction, transaction);
        this.mRuntimeId = 0L;
        this.mActionType = ActionType.Interact_1;
        this.mSlot = -1;
        this.mItem = new ItemStack(transaction.itemRegistry);
        this.mFromPos = new Vec3();
        this.mHitPos = new Vec3();
    }

    public final ItemUseOnActorInventoryTransaction.ActionType getActionType() {
        return mActionType;
    }

    public final long getEntityRuntimeId() {
        return mRuntimeId;
    }

    public final Vec3 getFromPosition() {
        return mFromPos;
    }

    public final Vec3 getHitPosition() {
        return mHitPos;
    }

    public final ItemStack getSelectedItem() {
        return mItem;
    }

    public int getSelectedSlot() {
        return mSlot;
    }
    
    public final ItemUseOnActorInventoryTransaction setSelectedItem(final ItemStack item) {
        this.mItem = item;
        return this;
    }

    public final ItemUseOnActorInventoryTransaction setSelectedSlot(int slot) {
        this.mSlot = slot;
        return this;
    }

    public final ItemUseOnActorInventoryTransaction setActionType(ItemUseOnActorInventoryTransaction.ActionType type) {
        this.mActionType = type;
        return this;
    }

    public final ItemUseOnActorInventoryTransaction setEntityRuntimeId(long entity) {
        this.mRuntimeId = entity;
        return this;
    }

    public final ItemUseOnActorInventoryTransaction setFromPosition(final Vec3 pos) {
        this.mFromPos = pos;
        return this;
    }

    public final ItemUseOnActorInventoryTransaction setHitPosition(final Vec3 pos) {
        this.mHitPos = pos;
        return this;
    }

    public enum ActionType {
        Interact_1,
        Attack_2,
        ItemInteract
    }
}
