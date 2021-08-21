package com.ddf.fakeplayer.container;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.item.ItemDescriptor;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.MathUtil;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;

public class FillingContainer extends Container {
    protected ArrayList<ItemStack> mItems = new ArrayList<>();
    protected Player mPlayer;

    public FillingContainer(Player player, int numTotalSlots, ContainerType containerType) {
        super(containerType);
        this.mPlayer = player;
        for (int i = 0; i < numTotalSlots; i++) {
            mItems.add(ItemStack.EMPTY_ITEM);
        }
        mItems.trimToSize();
    }

    void _release(int slot) {
        if (this.mItems.get(slot) != null && this.mItems.get(slot).toBoolean())
            this.setItem(slot, ItemStack.EMPTY_ITEM);
    }

    protected final int _getEmptySlotsCount(int start, int end) {
        int numEmpty = 0;
        if (end >= this.getContainerSize())
            end = this.getContainerSize();
        while (start < end) {
            ItemStack item = this.getItem(start);
            if (item != null && !item.toBoolean())
                ++numEmpty;
            ++start;
        }
        return numEmpty;
    }

    public final int getHotbarSize() {
        return 9;
    }

    @Override
    public int getItemCount(final ItemDescriptor descriptor) {
        int itemCount = 0;
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack item = this.getItem(i);
            if (item != null && item.toBoolean() && descriptor.sameItemAndAux(item))
                itemCount += item.getStackSize();
        }
        return itemCount;
    }

    @NotImplemented
    public final boolean getAndRemoveResource(ItemStack item, boolean requireExactAux, boolean requireExactData) {
        return false;
//        int slot;
//        if (Recipe.isAnyAuxValue(item.getDescriptor()) || item.getAuxValue() == 0x7FFF)
//            slot = this.getSlotWithItem(item, false, false);
//        else
//            slot = this.getSlotWithItem(item, requireExactAux, requireExactData);
//        if (slot >= 0) {
//            ItemStack rhs = this.getItem(slot);
//            ItemStack slotItem = new ItemStack(rhs);
//            item.set(slotItem);
//            item.setStackSize((byte) 1);
//            slotItem.remove(1);
//            this.setItem(slot, slotItem);
//            return true;
//        } else {
//            return false;
//        }
    }

    public final int getSlotWithItem(final ItemStack item, boolean checkAux, boolean checkData) {
        if (this.mPlayer != null) {
            int selectedSlot = this.mPlayer.getSelectedItemSlot();
            ItemStack selectedItem = this.getItem(selectedSlot);
            if (selectedItem != null && selectedItem.toBoolean()) {
                if (selectedItem.getId() == item.getId() && (!checkAux || selectedItem.hasSameAuxValue(item)) && (!checkData || selectedItem.hasSameUserData(item) && selectedItem.componentsMatch(item))) {
                    return selectedSlot;
                }
            }
        }
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack slotItem = this.getItem(i);
            if (slotItem != null && slotItem.toBoolean()) {
                if (slotItem.getId() == item.getId() && (!checkAux || slotItem.hasSameAuxValue(item)) && (!checkData || slotItem.hasSameUserData(item) && slotItem.componentsMatch(item))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < this.mItems.size())
            return this.mItems.get(slot);
        else
            return ItemStack.EMPTY_ITEM;
    }

    @Override
    public void setItem(int slot, final ItemStack item) {
        if (slot >= 0 && slot < this.mItems.size()) {
            if (this.mItems.get(slot) == null || !this.mItems.get(slot).equals(item)) {
                this.triggerTransactionChange(slot, this.mItems.get(slot), item);
                if (this.mPlayer != null && !this.mItems.get(slot).equals(item)) {
                    this.mPlayer.inventoryChanged(this, slot, this.mItems.get(slot), item);
                }
                this.mItems.set(slot, item);
                if (!item.toBoolean())
                    this.clearSlot(slot);
                this.setContainerChanged(slot);
            }
        }
    }

    @Override
    public int getContainerSize() {
        return this.mItems.size();
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void startOpen(Player player) {

    }

    @Override
    public void stopOpen(Player player) {

    }

    public void clearSlot(int slot) {
        if ( slot >= 0 && slot <= this.mItems.size())
            this._release(slot);
    }

    public int clearInventory(int resizeTo) {
        int itemsCleared = 0;
        for (int i = 0; i < this.getContainerSize(); ++i) {
            if (this.mItems.get(i) != null && this.mItems.get(i).toBoolean()) {
                itemsCleared += this.mItems.get(i).getStackSize();
            }
            this._release(i);
        }
        int v5;
        if (resizeTo >= 0)
            v5 = resizeTo;
        else
            v5 = this.getContainerSize();
        ArrayList<ItemStack> tempItems = this.mItems;
        this.mItems = new ArrayList<>(v5);
        for (int i = 0; i < v5; i++) {
            this.mItems.add(i, tempItems.get(i));
        }
        return itemsCleared;
    }

    public void doDrop(ItemStack item, boolean randomly) {
        if (this.mPlayer != null)
            this.mPlayer.drop(item, randomly);
    }

    public final void dropAll(int start, int end, boolean onlyClearContainer) {
        start = MathUtil.clamp(start, 0, this.getContainerSize());
        end = MathUtil.clamp(end, 0, this.getContainerSize());
        for (int i = start; i < end; ++i) {
            this.dropSlot(i, onlyClearContainer, true, true);
            this.setContainerChanged(i);
        }
    }

    public final void dropSlot(int slot, boolean onlyClearContainer, boolean dropAll, boolean randomly) {
        if (slot >= 0 && slot < this.mItems.size()) {
            ItemStack item = this.mItems.get(slot);
            if (item != null && item.toBoolean() && !item.isEmptyStack()) {
                int count = 1;
                if (dropAll) {
                    count = this.mItems.get(slot).getStackSize();
                }
                if (!onlyClearContainer) {
                    ItemStack dropItem = new ItemStack(this.mItems.get(slot));
                    dropItem.setStackSize((byte) count);
                    this.doDrop(dropItem, randomly);
                }
                ItemStack itemCopy = this.mItems.get(slot).clone();
                itemCopy.remove(count);
                this.setItem(slot, itemCopy);
                if (this.mItems.get(slot).isEmptyStack())
                    this._release(slot);
                this.setContainerChanged(slot);
            }
        }
    }
}
