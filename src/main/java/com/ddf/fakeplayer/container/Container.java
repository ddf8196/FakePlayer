package com.ddf.fakeplayer.container;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.item.ItemDescriptor;
import com.ddf.fakeplayer.item.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class Container {
    private ContainerType mContainerType;
    private HashSet<ContainerContentChangeListener> mContentChangeListeners = new HashSet<>();
    private HashSet<ContainerSizeChangeListener> mSizeChangeListeners = new HashSet<>();
    private ArrayDeque<TransactionContext> mTransactionContextStack = new ArrayDeque<>();
    private String mName;
    private boolean mCustomName;

    protected Container(ContainerType type) {
        this.mContainerType = type;
        this.mName = "";
        this.mCustomName = false;
    }

    protected Container(ContainerType type, final String name, boolean customName) {
        this.mContainerType = type;
        this.mName = name;
        this.mCustomName = customName;
    }

    public void init() {
    }

    public void addContentChangeListener(ContainerContentChangeListener listener) {
        this.mContentChangeListeners.add(listener);
    }

    public void createTransactionContext(final TransactionContext a2, Runnable a3) {
        this.mTransactionContextStack.addFirst(a2);
        a3.run();
        this.mTransactionContextStack.removeFirst();
    }

    public abstract ItemStack getItem(int slot);
    public abstract void setItem(int slot, final ItemStack item);
    public abstract int getContainerSize();
    public abstract int getMaxStackSize();
    public abstract void startOpen(Player player);
    public abstract void stopOpen(Player player);

    public int getItemCount(final ItemDescriptor descriptor) {
        int itemCount = 0;
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack item = this.getItem(i);
            if (item != null && item.toBoolean() && descriptor.sameItemAndAux(item))
                itemCount += item.getStackSize();
        }
        return itemCount;
    }

    public ArrayList<ItemStack> getSlots() {
        ArrayList<ItemStack> retstr = new ArrayList<>();
        for (int i = 0; i < this.getContainerSize(); ++i) {
            retstr.add(this.getItem(i));
        }
        return retstr;
    }

    public ArrayList<ItemStack> getSlotCopies() {
        ArrayList<ItemStack> retstr = new ArrayList<>();
        for (int i = 0; i < this.getContainerSize(); ++i) {
            retstr.add(new ItemStack(this.getItem(i)));
        }
        return retstr;
    }

    public void setContainerChanged(int slot) {
        for (ContainerContentChangeListener listener : this.mContentChangeListeners) {
            listener.containerContentChanged(slot);
        }
    }

    public void triggerTransactionChange(int slot, final ItemStack oldItem, final ItemStack newItem) {
        if (!oldItem.equals(newItem)) {
            for (TransactionContext f : this.mTransactionContextStack) {
                f.call(this, slot, oldItem, newItem);
            }
        }
    }

    @FunctionalInterface
    public interface TransactionContext {
        void call(Container container, int slot, final ItemStack oldItem, final ItemStack newItem);
    }
}
