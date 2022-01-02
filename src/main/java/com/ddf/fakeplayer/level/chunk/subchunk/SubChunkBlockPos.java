package com.ddf.fakeplayer.level.chunk.subchunk;

import com.ddf.fakeplayer.level.chunk.ChunkBlockPos;

public final class SubChunkBlockPos {
    public int x;
    public int y;
    public int z;
    //int packed;

    public SubChunkBlockPos(final ChunkBlockPos pos) {
        this.x = pos.x;
        this.y = pos.y & 0xF;
        this.z = pos.z;
//        this.packed &= 0xFFFFFF;
    }

    public final int getPacked() {
        return (x << 16) | (y << 8) | z;
    }

    public final short index() {
        return (short) ((this.x << 8) + 16 * this.z + this.y);
    }
}
