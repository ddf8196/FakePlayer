package com.ddf.fakeplayer.block.blocktypes;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.block.Material;
import com.ddf.fakeplayer.block.MaterialType;
import com.ddf.fakeplayer.nbt.CompoundTag;

public final class UnknownBlock extends BlockLegacy {

    public UnknownBlock(String name, int id, CompoundTag defaultState) {
        super(name, id, Material.getMaterial(MaterialType.Air));
        addState(defaultState);
    }

    public final void addState(CompoundTag state) {
        int data = mBlockPermutations.size();
        Block blockState = new Block((short) data, this);
        blockState.mSerializationId = state;
        if (data == 0)
            setDefaultState(blockState);
        mBlockPermutations.add(blockState);
    }
}
