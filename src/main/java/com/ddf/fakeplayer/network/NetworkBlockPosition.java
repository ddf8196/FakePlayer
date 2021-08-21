package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.block.BlockPos;

public class NetworkBlockPosition extends BlockPos {
    public NetworkBlockPosition() {
        this(0, 0, 0);
    }

    public NetworkBlockPosition(final BlockPos pos) {
        this(pos.x, pos.y, pos.z);
    }

    public NetworkBlockPosition(int _x, int _y, int _z) {
        super(_x, _y, _z);
    }
}
