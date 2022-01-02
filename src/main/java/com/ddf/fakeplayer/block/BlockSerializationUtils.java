package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;
import com.nukkitx.nbt.NbtMap;

import java.util.Objects;

@NotImplemented
public class BlockSerializationUtils {
    public static Block tryGetBlockFromNBT(final CompoundTag tag, BlockSerializationUtils.NbtToBlockCache localCache) {
        CompoundTag block = tag.getCompound("block");
        String name = block.getString("name");
        CompoundTag states = block.getCompound("states");
        BlockLegacy blockLegacy = BlockTypeRegistry.lookupByName(name);
        if (blockLegacy != null) {
            for (Block block1 : blockLegacy.mBlockPermutations) {
                CompoundTag states1 = block1.mSerializationId.getCompound("states");
                if (states1.size() == states.size()) {
                    if (Objects.equals(states1, states)) {
                        return block1;
                    }
                } else if (states1.size() > states.size()) {
                    if (states1.rawView().values().containsAll(states.rawView().values())) {
                        return block1;
                    }
                } else {
                    if (states.rawView().values().containsAll(states1.rawView().values())) {
                        return block1;
                    }
                }
            }
        }
        return BedrockBlocks.mAir;
    }

    @NotImplemented
    public static class NbtToBlockCache {
    }
}
