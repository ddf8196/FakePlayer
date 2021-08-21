package com.ddf.fakeplayer.container;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.item.ItemStack;

import java.util.ArrayList;

public class SimpleContainer extends Container {
    private int mSize;
    private ArrayList<ItemStack> mItems = new ArrayList<>();

    public SimpleContainer(String name, boolean customName, int size, ContainerType containerType) {
        super(containerType, name, customName);
        this.mSize = size;
        this.mItems.ensureCapacity(this.mSize);
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < this.mItems.size())
            return this.mItems.get(slot);
        else
            return ItemStack.EMPTY_ITEM;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < this.mItems.size()) {
            ItemStack newItem = new ItemStack(item);
            if (newItem.getStackSize() > this.getMaxStackSize()) {
                newItem.setStackSize((byte) this.getMaxStackSize());
            }
            this.triggerTransactionChange(slot, this.mItems.get(slot), newItem);
            this.mItems.set(slot, newItem);
            this.setContainerChanged(slot);
        }
    }

    @Override
    public int getContainerSize() {
        return this.mSize;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void startOpen(Player player) {
    }

    @Override
    public void stopOpen(Player player) {
    }
}
