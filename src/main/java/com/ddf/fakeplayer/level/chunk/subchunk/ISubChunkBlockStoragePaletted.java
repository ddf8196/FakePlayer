package com.ddf.fakeplayer.level.chunk.subchunk;

import com.ddf.fakeplayer.block.Block;

public interface ISubChunkBlockStoragePaletted extends SubChunkBlockStorage {
    void appendToPalette(final Block block);
    int[] getBlocks();
    int getBitsPerBlock();
    Block[] getPalette();
}
