package com.ddf.fakeplayer.level.chunk;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.block.VanillaBlocks;
import com.ddf.fakeplayer.util.mc.Bounds;

import java.util.Arrays;

public class WorldLimitChunkSource extends ChunkSource {
    private LevelChunk mInvisibleWallChunk;
    private final Bounds mLimitArea;

    public WorldLimitChunkSource(ChunkSource storage, final BlockPos center) {
        super(storage);
        ChunkPos centerChunkPos = new ChunkPos(center);
        ChunkPos min = centerChunkPos.subtract(8);
        ChunkPos max = centerChunkPos.add(7); //+8-1
        this.mLimitArea = new Bounds(min, max);

        this.mInvisibleWallChunk = LevelChunk.createNew(this.mDimension, ChunkPos.INVALID, true);

        Block[] blocks = new Block[this.getDimension().getHeight() << 8];
        Arrays.fill(blocks, VanillaBlocks.mInvisibleBedrock);

        this.mInvisibleWallChunk.setAllBlocks(blocks, this.getDimension().getHeight());
        this.mInvisibleWallChunk.changeState(ChunkState.Unloaded, ChunkState.Loaded);
        this.mInvisibleWallChunk.onTickingStarted();
    }
}
