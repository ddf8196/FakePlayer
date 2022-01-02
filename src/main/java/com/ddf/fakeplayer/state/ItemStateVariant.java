package com.ddf.fakeplayer.state;

import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;

@NotImplemented
public class ItemStateVariant extends ItemState {
    @NotImplemented
    public ItemStateVariant(int id, String stateName, int variationCount) {
        super(id, stateName, variationCount);
    }

    @NotImplemented
    @Override
    public void toNBT(CompoundTag tag, int val) {

    }

    @NotImplemented
    @Override
    public int fromNBT(CompoundTag tag) {
        return 0;
    }
}
