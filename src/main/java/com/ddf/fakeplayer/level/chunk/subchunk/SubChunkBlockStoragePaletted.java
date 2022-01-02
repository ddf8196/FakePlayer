package com.ddf.fakeplayer.level.chunk.subchunk;

import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockFetchResult;
import com.ddf.fakeplayer.block.BlockPalette;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.util.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class SubChunkBlockStoragePaletted implements ISubChunkBlockStoragePaletted {
    private final int[] mBlocks;
    private final Block[] mPalette;
    private final AtomicInteger mPaletteSize = new AtomicInteger();
    private final SubChunkBlockStorage.Type type;

    private SubChunkBlockStoragePaletted(Type type) {
        this.type = type;
        int blocksPerInt = 32 / type.getBitsPerBlock();
        this.mBlocks = new int[(int) Math.ceil(4096.0 / blocksPerInt)];
        this.mPalette = new Block[(int) Math.pow(2, type.getBitsPerBlock())];
    }

    public SubChunkBlockStoragePaletted(Type type, Block initBlock) {
        this(type);
        this.mPaletteSize.set(1);
        this.mPalette[0] = initBlock;
        Arrays.fill(this.mBlocks, 0);
    }

    @NotImplemented
    public SubChunkBlockStoragePaletted(Type type, final SubChunkBlockStorage upgradeFrom) {
        this(type);
//        this.mPaletteSize.set(0);
//        if (upgradeFrom != null) {
//            ISubChunkBlockStoragePaletted paletted = upgradeFrom.asPalettedStorage();
//            int[] src = paletted.getBlocks();
//            int srcBitsPerBlock = paletted.getBitsPerBlock();
//            copyPaletteIDs(this.mBlocks, type.getBitsPerBlock(), src, srcBitsPerBlock);
//            this.mPaletteSize.set(paletted.getPalette().length);
//            System.arraycopy(paletted.getPalette(), 0, this.mPalette, 0, this.mPaletteSize.get());
//        }
    }

    public static long packedWordsCount(int blocksPerWord) {
        int count = 4096 / blocksPerWord;
        if (4096 % blocksPerWord != 0)
            count += 1;
        return count;
    }

    @NotImplemented
    @Override
    public void appendToPalette(Block block) {

    }

    @Override
    public int[] getBlocks() {
        return mBlocks;
    }

    @Override
    public int getBitsPerBlock() {
        return type.getBitsPerBlock();
    }

    @Override
    public Block[] getPalette() {
        return mPalette;
    }

    @NotImplemented
    @Override
    public boolean isUniform(Block block) {
        return false;
    }

    @NotImplemented
    @Override
    public Block getBlock(short index) {
        return null;
    }

    @NotImplemented
    @Override
    public void setBlock(short index, Block block) {

    }

    @Override
    public long getBlockTypeCapacity() {
        return mPalette.length;
    }

    @Override
    public Type getType() {
        return type;
    }

    @NotImplemented
    @Override
    public SubChunkBlockStorage makePrunedCopy() {
        return null;
    }

    @Override
    public ISubChunkBlockStoragePaletted asPalettedStorage() {
        return this;
    }

    @NotImplemented
    @Override
    public void fetchBlocksInCylinder(BlockPos positionOfChunk, BlockPos pos, int radius, int height, Function<Block, Boolean> predicate, ArrayList<BlockFetchResult> output) {
    }

    @NotImplemented
    @Override
    public void fetchBlocksInBox(BlockPos positionOfChunk, BoundingBox box, Function<Block, Boolean> predicate, ArrayList<BlockFetchResult> output) {

    }

    @NotImplemented
    @Override
    public void _setAllBlocks(Block[] fullChunk, int sourceOffset, int sourceStride, Block[] palette) {

    }

    @Override
    public void _deserialize(IDataInput stream, BlockPalette globalPalette, boolean network) {
        byte[] bytes = stream.readBytes(this.mBlocks.length * Integer.BYTES);
        for (int i = 0; i < this.mBlocks.length; ++i) {
            int block = 0;
            block |= bytes[i * 4] & 0xFF;
            block |= bytes[i * 4 + 1] & 0xFF << 8;
            block |= bytes[i * 4 + 2] & 0xFF << 16;
            block |= bytes[i * 4 + 3] & 0xFF << 24;
            this.mBlocks[i] = block;
        }
        int size = MathUtil.clamp(stream.readInt(), 1, this.mPalette.length);
        if (network) {
            for (int i = 0; i < size; ++i) {
                int runtimeId = stream.readInt();
                this.mPalette[i] = globalPalette.getBlock(runtimeId);
            }
        } else {
            throw new RuntimeException("Not implemented");
        }
        this.mPaletteSize.set(size);
        this._zeroIndicesGreaterEqualThan((short) size);
    }

    @NotImplemented
    @Override
    public void _serialize(IDataOutput stream, boolean network) {

    }

    private void setBlock(/*uint16_t*/short index, /*uint16_t*/short pid) {
        this.mBlocks[index >> 5] = ((pid & 1) << (index & 0x1F)) | ~(1 << (index & 0x1F)) & this.mBlocks[index >> 5];
    }

    private void _zeroIndicesGreaterEqualThan(/*uint16_t*/short max) {
        List<Short> outOfBoundSlots = new ArrayList<>();
        int idx = 0;

        int readBlocks = 0;
        for (int word : this.mBlocks) {
            for (int i = 0; i < 32 / type.getBitsPerBlock(); ++i) {
                int block = word & type.getMask();
                if (block >= max) {
                    outOfBoundSlots.add((short) idx);
                }
                ++idx;

                word >>= type.getBitsPerBlock();
                if (++readBlocks == 4096)
                    break;
            }
        }

        for (Short index : outOfBoundSlots) {
            this.setBlock(index, (short) 0);
        }
    }
}
