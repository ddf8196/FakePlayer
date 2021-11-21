package com.ddf.fakeplayer.level.chunk;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.level.dimension.Dimension;
import com.ddf.fakeplayer.util.BoundingBox;
import com.ddf.fakeplayer.util.NotImplemented;

@NotImplemented
public class LevelChunk {
    @NotImplemented
    public LevelChunk(Dimension dimension, final ChunkPos cp, boolean readOnly) {

    }

    @NotImplemented
    public final Block getExtraBlock(ChunkBlockPos localPos) {
        return null;
    }

    public static class HardcodedSpawningArea {
        BoundingBox aabb;
        HardcodedSpawnAreaType type;
    }

    public enum Tag {
        Data2D(0x2D),
        Data2DLegacy(0x2E),
        SubChunkPrefix(0x2F),
        LegacyTerrain(0x30),
        BlockEntity_1(0x31),
        Entity_6(0x32),
        PendingTicks_0(0x33),
        LegacyBlockExtraData(0x34),
        BiomeState_0(0x35),
        FinalizedState(0x36),
        ConversionData(0x37),
        BorderBlocks_0(0x38),
        HardcodedSpawners(0x39),
        RandomTicks_0(0x3A),
        Version(0x76);

        private final int tag;

        Tag(int tag) {
            this.tag = tag;
        }

        public int getTag() {
            return tag;
        }
    }

    public enum Finalization {
        NeedsInstaticking,
        NeedsPopulation,
        Done
    }
}
