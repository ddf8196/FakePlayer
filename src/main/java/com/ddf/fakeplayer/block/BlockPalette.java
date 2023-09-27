package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.DataConverter;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Pair;
import org.cloudburstmc.nbt.NbtMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockPalette {
    private ReentrantLock mLegacyBlockStatesConversionWarningMutex = new ReentrantLock();
    private Set<Pair<Integer, Integer>> mLegacyBlockStatesConversionWarningSet = new HashSet<>();

    private Map<String, BlockLegacy> mNameLookup = new HashMap<>();
    private Map<CompoundTag, Block> mBlockFromSerId = new HashMap<>();

    private ArrayList<Block> mBlockFromRuntimeId = new ArrayList<>(0x2000);
    private Level mLevel;

    public BlockPalette() {
        this.mLevel = null;
    }

    public BlockPalette(Level level) {
        this.mLevel = level;
    }

    public final Block getBlock(final /*BlockRuntimeId*/int runtimeId) {
        if (!this.mBlockFromRuntimeId.isEmpty() && runtimeId < this.mBlockFromRuntimeId.size()) {
            return this.mBlockFromRuntimeId.get(runtimeId);
        }
        String str = "BlockRuntimeId (" + runtimeId + ") not found in the block palette, returning an air block instead. It is likely that the client and server didn't agree on the block palette they are using.";
        return BedrockBlocks.mAir;
    }

    public final Block getBlock(final CompoundTag serId) {
        Block block = BlockSerializationUtils.tryGetBlockFromNBT(serId, null);
        if (block != null)
            return block;
//        if (ServiceLocator<ContentLog>.isSet()){
//            ContentLog contentLog = ServiceLocator<ContentLog>.get();
//            if (contentLog.isEnabled()) {
//                contentLog.log(LogLevel.Warning, LogArea.Blocks, "%s", "BlockSerializationId with name: " + serId.getString("name") + " not found in the block palette. Returning an air block instead.");
//            }
//        }
        return BedrockBlocks.mAir;
    }

    public final void appendBlock(final Block blockState) {
        BlockLegacy block = blockState.getLegacyBlock();
        this.mNameLookup.put(block.getFullName(), block);
        int runtimeId = this.mBlockFromRuntimeId.size();
        blockState.setRuntimeId(runtimeId);
        this.mBlockFromRuntimeId.add(blockState);
    }

    public final boolean convertLegacyBlocks(Block[] dst, byte[] blockIDs, byte[] data, int volume) {
        if (dst.length == volume && blockIDs.length == volume && 2 * data.length == volume ) {
            for (int i = 0; i < volume; ++i) {
                byte nibble = data[i >> 1];
                byte id = blockIDs[i];
                byte blockData;
                if ((i & 1) != 0)
                    blockData = (byte) (nibble >> 4);
                else
                    blockData = (byte) (nibble & 0xF);
                dst[i] = this.convertLegacyBlock(id, blockData);
            }
            return true;
        } else {
            return false;
        }
    }

    @NotImplemented
    public final Block convertLegacyBlock(final byte id, short data) {
        return BedrockBlocks.mAir;
//        Block block = VanillaBlockConversion.tryGetLegacyState(id, data);
//        if (block == null) {
//            block = VanillaBlockConversion.tryGetLegacyState(id, 0);
//            if (block == null)
//                return BedrockBlocks.mAir;
//        }
//        return block;
    }

    public final void initFromNbtMapList(List<NbtMap> nbtMapList) {
        for (NbtMap nbtMap : nbtMapList) {
            CompoundTag tag = DataConverter.compoundTag(nbtMap);
            Block block = BlockSerializationUtils.tryGetBlockFromNBT(tag, null);
            if (block == null) {
                mNameLookup.clear();
                mBlockFromRuntimeId.clear();
                return;
            }
            appendBlock(block);
        }
    }
}
