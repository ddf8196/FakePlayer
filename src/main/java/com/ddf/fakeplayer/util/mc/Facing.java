package com.ddf.fakeplayer.util.mc;

import com.ddf.fakeplayer.actor.ActorFlags;
import com.ddf.fakeplayer.block.BlockPos;

public class Facing {
    public static final BlockPos[] DIRECTION = new BlockPos[6];

    public enum Name {
        DOWN((byte) 0x0),
        UP((byte) 0x1),
        NORTH_0((byte) 0x2),
        SOUTH_0((byte) 0x3),
        WEST_0((byte) 0x4),
        EAST_0((byte) 0x5),
        MAX((byte) 0x6),
        NOT_DEFINED((byte) 0x6),
        NUM_CULLING_IDS((byte) 0x7);

        private final byte value;

        Name(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static Name getByValue(byte value) {
            for (Name name : values()) {
                if (name.getValue() == value) {
                    return name;
                }
            }
            return null;
        }
    }

}
