package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.util.NotImplemented;

import java.util.HashMap;

@NotImplemented
public class BlockTypeRegistry {
    private static HashMap<String, BlockLegacy> mBlockLookupMap = new HashMap<>();

    public static BlockLegacy registerBlock(BlockLegacy block) {
        return mBlockLookupMap.put(block.getRawNameId().toLowerCase(), block);
    }

    @NotImplemented
    public static BlockLegacy lookupByName(String blockName) {
        if (blockName == null || blockName.isEmpty())
            return null;
        blockName = blockName.toLowerCase();
        if (mBlockLookupMap.containsKey(blockName))
            return mBlockLookupMap.get(blockName);
        int index = blockName.indexOf(':');
        if (index != -1) {
            blockName = blockName.substring(index + 1);
        }
        return mBlockLookupMap.get(blockName);
    }

    public static void prepareBlocks(/*uint32_t*/int latestUpdaterVersion) {
        for (BlockLegacy block : mBlockLookupMap.values()) {
            if (block != null) {
                block.createBlockPermutations(latestUpdaterVersion);
                block.init();
            }
        }
    }
}
