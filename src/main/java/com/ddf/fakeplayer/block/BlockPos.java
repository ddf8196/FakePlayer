package com.ddf.fakeplayer.block;

import com.ddf.fakeplayer.level.chunk.ChunkPos;
import com.ddf.fakeplayer.util.Vec3;

public class BlockPos {
    public static final BlockPos ZERO = new BlockPos();
    public static final BlockPos ONE = new BlockPos(1, 1, 1);
    public static final BlockPos MAX = new BlockPos(0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF);
    public static final BlockPos MIN = new BlockPos(0x80000000, 0x80000000, 0x80000000);

    public int x, y, z;

    public BlockPos() {
        this(0);
    }

    public BlockPos(int a) {
        this.x = a;
        this.y = a;
        this.z = a;
    }

    public BlockPos(int _x, int _y, int _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }

    public BlockPos(float x, float y, float z) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    public BlockPos(double x, double y, double z) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    public BlockPos(final Vec3 v) {
        this(v.x, v.y, v.z);
    }

    public BlockPos(final ChunkPos cp, int y) {
        this(16 * cp.x, y, 16 * cp.z);
    }

    public BlockPos(BlockPos bp) {
        this(bp.x, bp.y, bp.z);
    }

    public BlockPos add(BlockPos bp) {
        return add(bp.x, bp.y, bp.z);
    }

    public BlockPos add(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    public BlockPos addAndSet(BlockPos bp) {
        return addAndSet(bp.x, bp.y, bp.z);
    }

    public BlockPos addAndSet(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public boolean equals(BlockPos rhs) {
        return this.x == rhs.x
                && this.y == rhs.y
                && this.z == rhs.z;
    }
}
