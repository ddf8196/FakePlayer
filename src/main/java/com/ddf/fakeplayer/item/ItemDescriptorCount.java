package com.ddf.fakeplayer.item;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;

public class ItemDescriptorCount extends ItemDescriptor {
    private /*uint16_t*/int mStackSize;

    public ItemDescriptorCount() {
        super();
        this.mStackSize = 0;
    }

    public ItemDescriptorCount(final Block block, /*uint16_t*/int stackSize) {
        super(block);
        this.mStackSize = stackSize;
    }

    public ItemDescriptorCount(final BlockLegacy block, /*uint16_t*/int stackSize) {
        super(block);
        this.mStackSize = stackSize;
    }

    public ItemDescriptorCount(final Item item, int auxValue, /*uint16_t*/int stackSize) {
        super(item, auxValue);
        this.mStackSize = stackSize;
    }

    public ItemDescriptorCount(final ItemDescriptor descriptor, /*uint16_t*/int stackSize) {
        super(descriptor);
        this.mStackSize = stackSize;
    }

    public ItemDescriptorCount(final ItemDescriptorCount a2) {
        super(a2);
        this.mStackSize = a2.mStackSize;
    }

    public final /*uint16_t*/int getStackSize() {
        return this.mStackSize;
    }
}
