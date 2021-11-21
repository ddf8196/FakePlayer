package com.ddf.fakeplayer.level.chunk;

import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.mc.Facing;

import java.util.Objects;

@NotImplemented
public final class ChunkBlockPos {
    public final /*uint8_t*/byte x;
    public final /*uint8_t*/byte z;
    public final short y;

    public ChunkBlockPos() {
        this(0);
    }

    public ChunkBlockPos(/*uint8_t*/int i) {
        this.x = (byte) i;
        this.z = (byte) i;
        this.y = (short) i;
    }

    public ChunkBlockPos(/*uint8_t*/int _x, /*int16_t Height*/int _y, /*uint8_t*/int _z) {
        this.x = (byte) _x;
        this.z = (byte) _z;
        this.y = (short) _y;
    }
    
    public ChunkBlockPos(final BlockPos pos) {
        this(pos.x & 0xF, pos.y, pos.z & 0xF);
    }

    public final ChunkBlockPos above() {
        return new ChunkBlockPos(this.x, this.y + 1, this.z);
    }

    public final ChunkBlockPos below() {
        return new ChunkBlockPos(this.x, this.y - 1, this.z);
    }

    public static ChunkBlockPos fromIndex(/*uint16_t*/short idx) {
        int high = (idx & 0xFF00) >> 8;
        return new ChunkBlockPos(high >> 4, idx & 0x00FF, high & 0x0F);
    }

    public final int index2D() {
        return this.x + 16 * this.z;
    }

    public final ChunkBlockPos neighbor(Facing.Name facing) {
        return new ChunkBlockPos(
                Facing.DIRECTION[facing.getValue()].x + this.x,
                Facing.DIRECTION[facing.getValue()].y + this.y,
                Facing.DIRECTION[facing.getValue()].z + this.z);
    }

    public final BlockPos add(final BlockPos p) {
        return new BlockPos(p.x + this.x, p.y + this.y, p.z + this.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkBlockPos that = (ChunkBlockPos) o;
        return x == that.x && z == that.z && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, y);
    }
}
