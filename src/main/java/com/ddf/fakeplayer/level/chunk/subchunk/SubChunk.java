package com.ddf.fakeplayer.level.chunk.subchunk;

import com.ddf.fakeplayer.block.BedrockBlocks;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockPalette;
import com.ddf.fakeplayer.level.DirtyTicksCounter;
import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.threading.LockGuard;
import com.ddf.fakeplayer.util.threading.SpinLock;

public final class SubChunk {
    private final DirtyTicksCounter mDirtyTicksCounter = new DirtyTicksCounter();
    private SubChunkBrightnessStorage mLight;
    private final SubChunkBlockStorage[] mBlocks = new SubChunkBlockStorage[2];
//    private final SubChunkBlockStorage[] mBlocksReadPtr = new SubChunkBlockStorage[2];
    private final SpinLock mWriteLock = new SpinLock();

    public SubChunk() {
    }

    public SubChunk(final Block initBlock, boolean maxSkyLight, boolean fullyLit, SpinLock spinLock) {
        this();
        this.initialize(initBlock, maxSkyLight, fullyLit, spinLock);
    }

    public final void initialize(final Block initBlock, boolean maxSkyLight, boolean fullyLit, SpinLock spinLock) {
        for (int i = 0; i < 2; ++i) {
//            this.mBlocksReadPtr[i] = null;
            this.mBlocks[i] = null;
        }
        try (LockGuard<SpinLock> writeLock = new LockGuard<>(this.mWriteLock)) {
            if (initBlock != null) {
                SubChunkBlockStorage newStorage = SubChunkBlockStorage.makeUniform(initBlock);
                this._replaceBlocks(0, newStorage, writeLock);
            } else {
                SubChunkBlockStorage newStorage = SubChunkBlockStorage.makeUniform(BedrockBlocks.mAir);
                this._replaceBlocks(0, newStorage, writeLock);
            }
        }
    }

    public final Block getBlock(short index) {
//        return this.mBlocksReadPtr[0].getBlock(index);
        return this.mBlocks[0].getBlock(index);
    }

    public final Block getExtraBlock(short index) {
//        return this.mBlocksReadPtr[1].getBlock(index);
        return this.mBlocks[1].getBlock(index);
    }

    public final void setAllBlocks(Block[] fullChunk, int sourceOffset, int sourceStride) {
        SubChunkBlockStorage storage = SubChunkBlockStorage.makeFromRawData(fullChunk, sourceOffset, sourceStride);
        try (LockGuard<SpinLock> writeLock = new LockGuard<>(this.mWriteLock)) {
            this._replaceBlocks(0, storage, writeLock);
        }
    }

    private void _setBlock(int layer, /*uint16_t*/short index, final Block block) {
        try (LockGuard<SpinLock> writeLock = new LockGuard<>(this.mWriteLock)) {
            SubChunkBlockStorage replacement;
            if (this.mBlocks[layer] == null) {
                replacement = SubChunkBlockStorage.makeUniform(BedrockBlocks.mAir);
                replacement.setBlock(index, block);
                this._replaceBlocks(layer, replacement, writeLock);
                return;
            }
            if (!this.mBlocks[layer].setBlock(index, block)) {
                SubChunkBlockStorage old = this.mBlocks[layer];
                replacement = SubChunkBlockStorage.makeExpanded(old);
                replacement.setBlock(index, block);
                this._replaceBlocks(layer, replacement, writeLock);
            }
        }
    }

    private void _resetLight(boolean maxSkyLight, boolean maxLight) {
        boolean v4 = true;
        if ( !maxSkyLight )
            v4 = maxLight;
        if ( v4 && this.mLight == null)
            this._createLightStorage();
        if (this.mLight != null) {
            this.mLight.reset(maxSkyLight, maxLight);
        }
    }

    private void _createLightStorage() {
        try (LockGuard<SpinLock> lock = new LockGuard<>(this.mWriteLock)) {
            if (this.mLight == null) {
                this.mLight = new SubChunkBrightnessStorage();
            }
        }
    }

    private void _replaceBlocks(/*uint8_t*/int layer, SubChunkBlockStorage newStorage, LockGuard<SpinLock> a4) {
        this.mBlocks[layer] = newStorage;
//        this.mBlocksReadPtr[layer] = this.mBlocks[layer];
    }

    public final void deserialize(IDataInput stream, final BlockPalette palette) {
        byte f = stream.readByte();
        if (f >= SubChunkFormat.values().length) {
            return;
        }
        SubChunkFormat format = SubChunkFormat.values()[f];
        byte layerCount = 1;
        if (format == SubChunkFormat.v1_3_0_2) {
            layerCount = (byte) Math.min(stream.readByte(), 2);
        }
        try (LockGuard<SpinLock> writeLock = new LockGuard<>(this.mWriteLock)) {
            for (int layer = 0; layer < (int)layerCount; ++layer ) {
                SubChunkBlockStorage newStorage = SubChunkBlockStorage.makeDeserialized(stream, palette, format);
                this._replaceBlocks(layer, newStorage, writeLock);
            }
            int layer = 0;
            while (layer < 2) {
                if (layer != 0) {
                    this._replaceBlocks(layer, null, writeLock);
                } else {
                    SubChunkBlockStorage newStorage = SubChunkBlockStorage.makeUniform(null);
                    this._replaceBlocks(0, newStorage, writeLock);
                }
                ++layer;
            }
        }
    }

    @NotImplemented
    public final void serialize(final IDataOutput stream, boolean network) {
    }
}
