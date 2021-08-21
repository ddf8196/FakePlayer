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

    public enum ActionType {
        Interact_1,
        Attack_2,
        ItemInteract
    }
}
