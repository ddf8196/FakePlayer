package com.ddf.fakeplayer.level.chunk.subchunk;

import com.ddf.fakeplayer.block.*;
import com.ddf.fakeplayer.util.BoundingBox;
import com.ddf.fakeplayer.util.IDataInput;
import com.ddf.fakeplayer.util.IDataOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface SubChunkBlockStorage {
    boolean isUniform(final Block block);
    Block getBlock(/*ushort*/short index);
    boolean setBlock(/*ushort*/short index, final Block block); //+4
    int getBlockTypeCapacity(); //+5
    SubChunkStorageUnit.Type getType();
    SubChunkBlockStorage makePrunedCopy();
    ISubChunkBlockStoragePaletted asPalettedStorage();
    void fetchBlocksInCylinder(final BlockPos positionOfChunk, final BlockPos pos, /*uint32_t*/int radius, /*uint32_t*/int height,
                               final Function<Block, Boolean> predicate,
                               ArrayList<BlockFetchResult> output);
    void fetchBlocksInBox(final BlockPos positionOfChunk, final BoundingBox box, final Function<Block, Boolean> predicate, ArrayList<BlockFetchResult> output);
    void _setAllBlocks(Block[] fullChunk, /*size_t*/int sourceOffset, /*size_t*/int sourceStride, final Block[] palette); //+12
    void _deserialize(IDataInput stream, final BlockPalette globalPalette, boolean network);
    void _serialize(IDataOutput stream, boolean network);

    static SubChunkBlockStorage makeDeserialized(IDataInput stream, final BlockPalette palette, SubChunkFormat format) {
        if (format != SubChunkFormat.v17_0_0) {
            if (format == SubChunkFormat.v1_3_0_0 || format == SubChunkFormat.v1_3_0_2) {
                SubChunkStorageUnit.SubChunkStorageFormat internalFormat = new SubChunkStorageUnit.SubChunkStorageFormat(stream.readByte());
                SubChunkBlockStorage ret = SubChunkBlockStorage.makeType(SubChunkStorageUnit.Type.getByValue(internalFormat.type), null);
                ret._deserialize(stream, palette, internalFormat.network);
                return ret;
            } else {
                return SubChunkBlockStorage.makeUniform(BedrockBlocks.mAir);
            }
        }
        byte[] ids = stream.readBytes(4096);
        byte[] data = stream.readBytes(2048);
        Block[] dst = new Block[4096];

        if (palette.convertLegacyBlocks(dst, ids, data, 4096)) {
            return SubChunkBlockStorage.makeFromRawData(dst, 0, 16);
        } else {
            return SubChunkBlockStorage.makeUniform(BedrockBlocks.mAir);
        }
    }

    static SubChunkBlockStorage makeUniform(final Block block) {
        return new SubChunkBlockStoragePaletted(SubChunkStorageUnit.Type.Paletted1, block);
    }

    static SubChunkBlockStorage makeType(SubChunkStorageUnit.Type type, final SubChunkBlockStorage old) {
        return new SubChunkBlockStoragePaletted(type, old);
    }

    static SubChunkBlockStorage makeFromRawData(Block[] fullChunk, int sourceOffset, int sourceStride) {
        int column = sourceOffset;
        List<Block> palette = new ArrayList<>();
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 16; ++j) {
                Block newBlock = fullChunk[column + j];
                if (!palette.contains(newBlock))
                    palette.add(newBlock);
            }
            column += sourceStride;
        }
        int blockCount = palette.size();
        SubChunkStorageUnit.Type type = SubChunkBlockStorage.chooseTypeForBlockTypeCount(blockCount);
        SubChunkBlockStorage ret = SubChunkBlockStorage.makeType(type, null);
        ret._setAllBlocks(fullChunk, sourceOffset, sourceStride, palette.toArray(new Block[0]));
        return ret;
    }

    static SubChunkBlockStorage makeExpanded(final SubChunkBlockStorage old) {
        SubChunkStorageUnit.Type type = SubChunkBlockStorage.chooseTypeForBlockTypeCount(old.getBlockTypeCapacity() + 1);
        return SubChunkBlockStorage.makeType(type, old);
    }

    static SubChunkStorageUnit.Type chooseTypeForBlockTypeCount(int blockCount) {
        if (blockCount <= 2)
            return SubChunkStorageUnit.Type.Paletted1;
        if (blockCount <= 4)
            return SubChunkStorageUnit.Type.Paletted2;
        if (blockCount <= 8)
            return SubChunkStorageUnit.Type.Paletted3;
        if (blockCount <= 16)
            return SubChunkStorageUnit.Type.Paletted4;
        if (blockCount <= 32)
            return SubChunkStorageUnit.Type.Paletted5;
        if (blockCount <= 64)
            return SubChunkStorageUnit.Type.Paletted6;
        if (blockCount <= 256)
            return SubChunkStorageUnit.Type.Paletted8;
        return SubChunkStorageUnit.Type.Paletted16;
    }

}
