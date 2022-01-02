package com.ddf.fakeplayer.container.inventory.transaction;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.network.NetworkBlockPosition;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;

public class ItemUseInventoryTransaction extends ComplexInventoryTransaction {
    private ItemUseInventoryTransaction.ActionType mActionType;
    private NetworkBlockPosition mPos;
    private int mTargetBlockId;
    private /*uint8_t FacingID*/int mFace;
    private int mSlot;
    private ItemStack mItem;
    private Vec3 mFromPos;
    private Vec3 mClickPos;

    public ItemUseInventoryTransaction() {
        super(Type.ItemUseTransaction);
        this.mActionType = ActionType.Place_4;
        this.mPos = new NetworkBlockPosition();
        this.mTargetBlockId = 0;
        this.mFace = 0;
        this.mSlot = 0;
        this.mItem = new ItemStack();
        this.mFromPos = new Vec3();
        this.mClickPos = new Vec3();
    }

    public ItemUseInventoryTransaction(final InventoryTransaction transaction) {
        super(Type.ItemUseTransaction, transaction);
        this.mActionType = ActionType.Place_4;
        this.mPos = new NetworkBlockPosition();
        this.mTargetBlockId = 0;
        this.mFace = 0;
        this.mSlot = 0;
        this.mItem = new ItemStack();
        this.mFromPos = new Vec3();
        this.mClickPos = new Vec3();
    }
    
    public final ItemUseInventoryTransaction.ActionType getActionType() {
        return this.mActionType;
    }

    public final NetworkBlockPosition getBlockPosition() {
        return this.mPos;
    }

    public final Vec3 getClickPosition() {
        return this.mClickPos;
    }

    public final /*uint8_t FacingID*/int getFacing() {
        return this.mFace;
    }

    public final Vec3 getFromPosition() {
        return this.mFromPos;
    }

    public final ItemStack getSelectedItem() {
        return this.mItem;
    }

    public final int getSelectedSlot() {
        return this.mSlot;
    }

    public final Block getTargetBlock(final Level level) {
        return null;
//        return level.getGlobalBlockPalette().getBlock(this.mTargetBlockId);
    }

    public final ItemUseInventoryTransaction setSelectedItem(final ItemStack item) {
        this.mItem = item;
        return this;
    }

    public final ItemUseInventoryTransaction setSelectedSlot(int slot) {
        this.mSlot = slot;
        return this;
    }

    public final ItemUseInventoryTransaction setBlockPosition(final NetworkBlockPosition pos) {
        this.mPos = pos;
        return this;
    }

    public final ItemUseInventoryTransaction setFacing(/*uint8_t FacingID*/int face) {
        this.mFace = face;
        return this;
    }

    public final ItemUseInventoryTransaction setClickPosition(final Vec3 pos) {
        this.mClickPos = pos;
        return this;
    }

    public final ItemUseInventoryTransaction setFromPosition(final Vec3 pos) {
        this.mFromPos = pos;
        return this;
    }

    public final ItemUseInventoryTransaction setActionType(ItemUseInventoryTransaction.ActionType type) {
        this.mActionType = type;
        return this;
    }

    @NotImplemented
    public final ItemUseInventoryTransaction setTargetBlock(final Block targetBlock) {
//        this.mTargetBlockId = targetBlock.getRuntimeId();
        return this;
    }
    
    public enum ActionType {
        Place_4,
        Use_1,
        Destroy
    }
}
