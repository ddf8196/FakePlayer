package com.ddf.fakeplayer.util.mc;

import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.level.chunk.ChunkPos;

public final class Bounds {
    private Pos mMin;
    private Pos mMax;
    private Pos mDim;
    private int mArea;
    private int mVolume;
    private int mSide;

    public Bounds(final ChunkPos min, final ChunkPos max) {
        this(new BlockPos(min, 0), new BlockPos(max, 0), 16, Option.Flatten);
    }

    public Bounds(final BlockPos minBlock, final BlockPos maxBlock, int side, Bounds.Option buildOption) {
        this.mMin = new Pos();
        this.mMax = new Pos();
        this.mDim = new Pos();

        this.mArea = 0;
        this.mVolume = 0;
        this.mSide = side;
        Pos v9 = this.gridToChunk(minBlock);
        Pos v8 = this.gridToChunk(maxBlock);
        if (buildOption == Option.Flatten) {
            v8.y = 0;
            v9.y = 0;
        }
        this.resize(v9, v8);
    }

    public final Pos gridToChunk(final BlockPos p) {
        return new Pos(p.x >> 4, p.y >> 4, p.z >> 4);
    }

    public final void resize(final Pos cmin, final Pos cmax) {
        this.mMin = cmin;
        this.mMax = cmax;
        this.mDim = this.mMax.subtract(this.mMin).add(1);
        this.mArea = this.mDim.z * this.mDim.x;
        this.mVolume = this.mDim.y * this.mArea;
    }

    public enum Option {
        Default_5,
        Flatten,
    }

//    public static class Iterator extends Pos {
//        final Bounds mBounds;
//        int mIdx;
//    }
}
