package com.ddf.fakeplayer.container.inventory;

import com.ddf.fakeplayer.container.*;
import com.ddf.fakeplayer.container.hud.HudContainerManagerModel;
import com.ddf.fakeplayer.item.ItemDescriptor;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;

public class PlayerInventoryProxy implements ContainerSizeChangeListener, ContainerContentChangeListener {
    private int mSelected;
    private ItemStack mInfiniteItem;
    private ContainerID mSelectedContainerId;
    public Inventory mInventory;
    private ArrayList<ItemStack> mComplexItems;
    private HudContainerManagerModel mHudContainerManager;

    public PlayerInventoryProxy(ItemRegistry itemRegistry, Inventory inv) {
        this.mSelected = 0;
        this.mInfiniteItem = new ItemStack(itemRegistry);
        this.mSelectedContainerId = ContainerID.CONTAINER_ID_INVENTORY;
        this.mInventory = inv;
        this.mComplexItems = new ArrayList<>();
    }

    public final void init(HudContainerManagerModel hud) {
        this.mHudContainerManager = hud;
        this.mInventory.init();
        this.mInventory.addContentChangeListener(this);
    }

    public void addListener(ContainerContentChangeListener listener) {
        this.mInventory.addContentChangeListener(listener);
    }

    public final void createTransactionContext(final Container.TransactionContext a2, final Runnable a3) {
        this.mInventory.createTransactionContext(a2, a3);
    }

    public final HudContainerManagerModel _getHudContainer() {
        return this.mHudContainerManager;
    }

    public final int getContainerSize(ContainerID containerId) {
        if (containerId != ContainerID.CONTAINER_ID_INVENTORY)
            return 0;
        return this.mInventory.getContainerSize();
    }

    public final int getHotbarSize() {
        return this.mInventory.getHotbarSize();
    }

    public final ItemStack getItem(int slot, ContainerID containerId) {
        if (containerId != ContainerID.CONTAINER_ID_INVENTORY)
            return ItemStack.EMPTY_ITEM;
        return this.mInventory.getItem(slot);
    }

    public final int getItemCount(ItemDescriptor descriptor) {
        return this.mInventory.getItemCount(descriptor);
    }

    public final ItemStack getSelectedItem() {
        if (this.mSelectedContainerId != ContainerID.CONTAINER_ID_INVENTORY)
            return ItemStack.EMPTY_ITEM;
        return this.mInventory.getItem(this.mSelected);
    }

    public final PlayerInventoryProxy.SlotData getSelectedSlot() {
        return new PlayerInventoryProxy.SlotData(this.mSelected, this.mSelectedContainerId);
    }

    public final ContainerID getSelectedContainerId() {
        return this.mSelectedContainerId;
    }

    public final ArrayList<ItemStack> getSlots() {
        return this.mInventory.getSlots();
    }

    public final ArrayList<ItemStack> getSlotCopies(ContainerID containerId) {
        ArrayList<ItemStack> result;
        if ( containerId == ContainerID.CONTAINER_ID_INVENTORY ) {
            result = new ArrayList<>();
            for (ContainerItemStack itemIter : this._getHudContainer().getItems()) {
                result.add(itemIter.getItemStack());
            }
        }

        return this.mInventory.getSlotCopies();
    }

    private static final ArrayList<ContainerID> getAllContainerIds_containerIDs = new ArrayList<ContainerID>() {{
        add(ContainerID.CONTAINER_ID_INVENTORY);
    }};
    public final ArrayList<ContainerID> getAllContainerIds() {
        return PlayerInventoryProxy.getAllContainerIds_containerIDs;
    }

    public final boolean getAndRemoveResource(ItemStack inout, boolean requireExactAux, boolean requireExactData) {
        return this.mInventory.getAndRemoveResource(inout, requireExactAux, requireExactData);
    }

    private static final ArrayList<ItemStack> getComplexItems_empty = new ArrayList<>(0);
    public final ArrayList<ItemStack> getComplexItems(ContainerID containerId) {
        if (containerId == ContainerID.CONTAINER_ID_INVENTORY)
            return this.mComplexItems;
        return getComplexItems_empty;
    }

    public int getEmptySlotsCount() {
        return this.mInventory.getEmptySlotsCount();
    }

    public int getFirstEmptySlot() {
        return this.mInventory.getFirstEmptySlot();
    }

    public final void setSelectedItem(final ItemStack item) {
        if (this.mSelectedContainerId == ContainerID.CONTAINER_ID_INVENTORY) {
            this.mInventory.setItem(this.mSelected, item);
        }
    }

    public final void setItem(int slot, final ItemStack item, ContainerID containerId) {
        if (containerId == ContainerID.CONTAINER_ID_INVENTORY) {
            this.mInventory.setItem(slot, item);
        }
    }

    public final void setItemWithoutSlotLinking(int slot, final ItemStack item, ContainerID containerId) {
        if (containerId == ContainerID.CONTAINER_ID_INVENTORY) {
            this.mInventory.setItem(slot, item);
        }
    }

    public final void selectSlot(int slot, ContainerID containerId) {
        if (slot < this.mInventory.getHotbarSize() && slot >= 0) {
            this.mSelected = slot;
            this.mSelectedContainerId = containerId;
        }
    }

    public final void clearSlot(int slot, ContainerID containerId) {
        if (containerId == ContainerID.CONTAINER_ID_INVENTORY) {
            this.mInventory.clearSlot(slot);
        }
    }

    public final void dropAll(boolean onlyClearContainer) {
        this.mInventory.dropAll(onlyClearContainer);
    }

    public final boolean dropSlot(int slot, boolean onlyClearContainer, boolean dropAll, ContainerID containerId, boolean randomly) {
        if (containerId != ContainerID.CONTAINER_ID_INVENTORY || !this.mInventory.getItem(slot).toBoolean()) {
            return false;
        } else {
            this.mInventory.dropSlot(slot, onlyClearContainer, dropAll, randomly);
            return true;
        }
    }

    public final void swapSlots(int from, int to) {
        this.mInventory.swapSlots(from, to);
    }

    public final void tick() {
        if (this.mSelectedContainerId == ContainerID.CONTAINER_ID_INVENTORY) {
            this.mInventory.tick(this.mSelected);
        }
    }

    @Override
    public void containerSizeChanged(int size) {
    }

    @NotImplemented
    @Override
    public void containerContentChanged(int slot) {
    }

    public static class SlotData {
        public ContainerID mContainerId;
        public int mSlot;

        public SlotData(int slot, ContainerID containerId) {
            this.mContainerId = containerId;
            this.mSlot = slot;
        }
    }
}
