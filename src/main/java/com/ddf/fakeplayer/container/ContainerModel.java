package com.ddf.fakeplayer.container;

import com.ddf.fakeplayer.item.ItemInstance;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.ArrayList;
import java.util.HashMap;

public class ContainerModel implements ContainerContentChangeListener {
    public static HashMap<ContainerEnumName, String> ContainerCollectionNameMap = new HashMap<>();

    private String mContainerStringName;
    private final ContainerEnumName mContainerEnumName;
    private ArrayList<ContainerItemStack> mItems;
    private ArrayList<ItemInstance> mItemInstances;
    private ArrayList<OnContainerChangedCallback> mOnContainerChangedCallbacks;
    private PlayerNotificationCallback mPlayerNotificationCallbacks;
    private ContainerCategory mContainerCategory;
    private  ArrayList<SlotData> mItemSource;

    public ContainerModel(ContainerEnumName containerName, int containerSize, ContainerCategory containerCategory) {
        this.mContainerEnumName = containerName;
        this.mItems = new ArrayList<>();
        this.mItemInstances = new ArrayList<>();
        this.mOnContainerChangedCallbacks = new ArrayList<>();
        this.mContainerCategory = containerCategory;
        this.mItemSource = new ArrayList<>();
        this.resize(containerSize);
        this.mContainerStringName = ContainerCollectionNameMap.get(this.mContainerEnumName);
        this._init();
    }

    private void _init() {
        int oldSize = this.mItems.size();
        this.mItems.clear();
        this.mItems.ensureCapacity(oldSize);
        if (this.isIntermediaryCategory()) {
            this.mItemSource.clear();
            this.mItemSource.ensureCapacity(oldSize);
        }
    }

    public void resize(int containerSize) {
        ArrayList<ContainerItemStack> tempItems = this.mItems;
        this.mItems = new ArrayList<>(containerSize);
        for (int i = 0; i < containerSize; i++) {
             this.mItems.add(i, tempItems.get(i));
        }
        if (this.isIntermediaryCategory()) {
            ArrayList<SlotData> tempItemSource = this.mItemSource;
            this.mItemSource = new ArrayList<>(containerSize);
            for (int i = 0; i < containerSize; i++) {
                this.mItemSource.add(i, tempItemSource.get(i));
            }
        }
    }

    public boolean isIntermediaryCategory() {
        return this.mContainerCategory == ContainerCategory.Intermediary;
    }

    @NotImplemented
    @Override
    public void containerContentChanged(int slot) {
    }

    public interface OnContainerChangedCallback {
        void call(int int1, final ItemStack itemStack1, final ItemStack itemStack2);
    }

    public interface PlayerNotificationCallback {
        void call(int int1, final ItemStack itemStack1, final ItemStack itemStack2);
    }
}