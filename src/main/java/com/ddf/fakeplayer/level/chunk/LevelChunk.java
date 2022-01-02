package com.ddf.fakeplayer.level.chunk;

import com.ddf.fakeplayer.block.BedrockBlocks;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.blockactor.BlockActor;
import com.ddf.fakeplayer.level.DirtyTicksCounter;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.level.chunk.subchunk.SubChunk;
import com.ddf.fakeplayer.level.chunk.subchunk.SubChunkBlockPos;
import com.ddf.fakeplayer.level.chunk.subchunk.SubChunkInitMode;
import com.ddf.fakeplayer.level.dimension.Dimension;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.nbt.NbtIo;
import com.ddf.fakeplayer.util.*;
import com.ddf.fakeplayer.util.mc.SharedConstants;
import com.ddf.fakeplayer.util.threading.SpinLock;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class LevelChunk {
    private Level mLevel;
    private Dimension mDimension;
    private BlockPos mMin;
    private BlockPos mMax;
    private ChunkPos mPosition;
    private boolean mLightingFixupDone;
    private AtomicBoolean mLightingTaskActive;
    private boolean mReadOnly;
    private ChunkSource mGenerator;
    private LevelChunkFormat mLoadedFormat;
    private String mSerializedEntitiesBuffer;
    private AtomicReference<ChunkState> mLoadState;
    private ChunkTerrainDataState mTerrainDataState;
    private ChunkDebugDisplaySavedState mDebugDisplaySavedState;
    private ChunkCachedDataState mCachedDataState;
    private SpinLock mCachedDataStateSpinLock;
    private long mLastTick;
//    std::unique_ptr<BlockTickingQueue,std::default_delete<BlockTickingQueue> > mTickQueue;
//    std::unique_ptr<BlockTickingQueue,std::default_delete<BlockTickingQueue> > mRandomTickQueue;
    /*AppendOnlyAtomicLookupTable<SubChunk,16>*/
    private List<SubChunk> mSubChunks = Collections.synchronizedList(new ArrayList<>(16));
    private SpinLock[] mSubChunkSpinLocks = new SpinLock[16];
    private byte[] mBiomes = new byte[256];
//    std::array<ColumnCachedData,256> mCachedData;
    private short[] mHeightmap = new short[256];
//    std::unique_ptr<std::vector<short,std::allocator<short> >,std::default_delete<std::vector<short,std::allocator<short> > > > mPreWorldGenHeightmap;
    private HashMap<Byte, BiomeChunkState> mBiomeStates = new HashMap<>();
    private boolean mHasCachedTemperatureNoise;
    private byte[] mBorderBlockMap = new byte[256];
    private LevelChunk.Finalization mFinalized;
    private boolean mIsRedstoneLoaded;
    private boolean mOwnedByTickingThread;
    private DirtyTicksCounter[] mFullChunkDirtyTicksCounters = new DirtyTicksCounter[6];
    private short[] mRainHeights = new short[256];
//    OwnedActorSet mEntities;
//    LevelChunk::OwnedBlockActorMap mBlockEntities;
//    LevelChunk::BlockActorVector mDeletedBlockEntities;
    private BrightnessPair mDefaultBrightness;
    private ArrayList<LevelChunk.HardcodedSpawningArea> mHardcodedSpawningAreas;
    /*uint8_t*/byte[][] mbChunkInterpolants = new byte[2][2];
    private boolean mbChunkHasConverterTag;
    private boolean mDBChunkSurroundedByNeighbors;

    public LevelChunk(Dimension dimension, final ChunkPos cp, boolean readOnly) {
        this.mLevel = dimension.getLevel();
        this.mDimension = dimension;
        this.mMin = new BlockPos(cp, 0);
        this.mMax = new BlockPos(cp, 0).addAndSet(15, dimension.getHeight() - 1, 15);
        this.mPosition = new ChunkPos(cp);
        this.mLightingFixupDone = false;
        this.mLightingTaskActive = new AtomicBoolean(false);
        this.mReadOnly = readOnly;
        this.mGenerator = null;
        this.mLoadedFormat = SharedConstants.CurrentLevelChunkFormat;
        this.mSerializedEntitiesBuffer = "";
        this.mLoadState = new AtomicReference<>(ChunkState.Unloaded);
        this.mTerrainDataState = ChunkTerrainDataState.NoData_0;
        this.mDebugDisplaySavedState = ChunkDebugDisplaySavedState.Generated_1;
        this.mCachedDataState = ChunkCachedDataState.NotGenerated;
        this.mCachedDataStateSpinLock = new SpinLock();
        this.mLastTick = 0;
//        this.mTickQueue = new BlockTickingQueue();
//        this.mRandomTickQueue = new BlockTickingQueue();
//        this.mSubChunkSpinLocks = new SpinLock[16];
//        this.mPreWorldGenHeightmap = new ArrayList<>();
//        this.mBiomeStates = new HashMap<>();
        this.mHasCachedTemperatureNoise = false;
        this.mFinalized = Finalization.NeedsInstaticking;
        this.mIsRedstoneLoaded = false;
        this.mOwnedByTickingThread = false;
        for (int index = 0; index < this.mFullChunkDirtyTicksCounters.length; index++) {
            this.mFullChunkDirtyTicksCounters[index] = new DirtyTicksCounter();
        }
//        this.mEntities = new ArrayList<>(0);
//        this.mBlockEntities = new HashMap<>();
//        this.mDeletedBlockEntities = new ArrayList<>();
        this.mDefaultBrightness = dimension.getDefaultBrightness();
        this.mHardcodedSpawningAreas = new ArrayList<>();
        this.mbChunkHasConverterTag = false;
        this.mDBChunkSurroundedByNeighbors = false;
        Arrays.fill(this.mHeightmap, (short) 0);
        /*unsigned __int8 BiomeChunkData*/int zeroBiomeChunkData = dimension.getDefaultBiome();
        Arrays.fill(this.mBiomes, (byte) zeroBiomeChunkData);
        Arrays.fill(this.mBorderBlockMap, (byte) 0);
        Arrays.fill(this.mRainHeights, (short) -999);
    }

    public static LevelChunk createNew(Dimension dimension, ChunkPos cp, boolean readOnly) {
        return new LevelChunk(dimension, cp, readOnly);
    }

    private SubChunk _createSubChunk(/*size_t*/int idx, boolean initSkyLight, SubChunkInitMode initBlocks) {
        SubChunk subChunk = null;
        for (int currentIdx = this.mSubChunks.size(); currentIdx <= idx; ++currentIdx) {
            Block defaultBlock = null;
            if (initBlocks == SubChunkInitMode.All) {
                defaultBlock = BedrockBlocks.mAir;
            } else if (initBlocks == SubChunkInitMode.AllButLast && currentIdx != idx) {
                defaultBlock = BedrockBlocks.mAir;
            }
            subChunk = new SubChunk(defaultBlock, initSkyLight && this.mDefaultBrightness.sky > Brightness.MIN, false, this.mSubChunkSpinLocks[this.mSubChunks.size()]);
            this.mSubChunks.add(subChunk);
        }
        return subChunk;
    }

    public final Block getBlock(final ChunkBlockPos pos) {
        SubChunk sc = this.mSubChunks.get(pos.y >> 4);
        if (sc == null)
            return BedrockBlocks.mAir;
        short blockIdx = new SubChunkBlockPos(pos).index();
        return sc.getBlock(blockIdx);
    }

    public final Block getExtraBlock(ChunkBlockPos localPos) {
        SubChunk sc = this.mSubChunks.get(localPos.y >> 4);
        if (sc == null)
            return BedrockBlocks.mAir;
        short blockIdx = new SubChunkBlockPos(localPos).index();
        return sc.getExtraBlock(blockIdx);
    }

    public boolean getBorder(final ChunkBlockPos pos) {
        return this.mBorderBlockMap[pos.index2D()] != 0;
    }

    public final ChunkPos getPosition() {
        return this.mPosition;
    }

    public final AtomicReference<ChunkState> getState() {
        return this.mLoadState;
    }

    public final List<SubChunk> getSubChunks() {
        return this.mSubChunks;
    }

    public final boolean isReadOnly() {
        return this.mReadOnly;
    }

    private short findHighestNonAirBlock(Block[] blocks, short sourceColumnHeight) {
        short highest = 0;
        int basePtr = 0;
        int end = blocks.length;
        while (basePtr < end) {
            for (int j = sourceColumnHeight - 1; j > highest; --j) {
                if (blocks[basePtr + j] != BedrockBlocks.mAir )
                    highest = (short) j;
            }
            basePtr += sourceColumnHeight;
        }
        return highest;
    }

    public final void setAllBlocks(Block[] blocks, short sourceColumnHeight) {
        int hidx = findHighestNonAirBlock(blocks, sourceColumnHeight);
        if (hidx != 0) {
            this._createSubChunk(hidx >> 4, false, SubChunkInitMode.None_12);
            int offset = 0;
            for (SubChunk sc : this.getSubChunks()) {
                sc.setAllBlocks(blocks, offset, sourceColumnHeight);
                offset += 16;
            }
        }
    }

    public final void changeState(ChunkState from, ChunkState to) {
        this.tryChangeState(from, to);
    }

    public final boolean tryChangeState(ChunkState from, ChunkState to) {
        return this.mLoadState.compareAndSet(from, to);
    }

    public final void deserializeSubChunk(/*uint8_t*/int idx, IDataInput stream) {
        SubChunk subChunk = this._createSubChunk(idx, false, SubChunkInitMode.AllButLast);
        subChunk.deserialize(stream, this.mLevel.getGlobalBlockPalette());
    }

    public final void deserializeBiomes(IDataInput stream) {
        stream.readBytes(this.mBiomes, 0, 256);
        this.checkBiomeStates();
    }

    public final void deserializeBorderBlocks(IDataInput stream) {
        for (byte count = stream.readByte(); count != 0; --count ) {
            byte index = stream.readByte();
            this.mBorderBlockMap[index] = 1;
        }
    }

    @NotImplemented
    public final void deserializeBlockEntities(IDataInput stream) {
//        DefaultDataLoadHelper dataLoadHelper = new DefaultDataLoadHelper();
        while (stream.numBytesLeft() > 0) {
            BlockActor e = null;
            CompoundTag et = NbtIo.read(stream);
//            if (et != null) {
//                e = BlockActor.loadStatic(this.mLevel, et, dataLoadHelper);
//            }
//
        }
    }

    @NotImplemented
    private void checkBiomeStates() {
//        Map<Byte, BiomeChunkState> oldBiomeStates = new HashMap<>(this.mBiomeStates);
//        this.mBiomeStates.clear();
//        for (int id : this.mBiomes) {
//            Biome biome = this.mLevel.getBiomeRegistry().lookupById(id);
//            if (biome == null) {
//                BiomeIdCompatibility.isReserved(id);
//                id = (*((__int64 (__fastcall **)(Dimension *))this->mDimension->_vptr$BlockSourceListener + 48))(this->mDimension);
//                biome = this.mLevel.getBiomeRegistry().lookupById(id);
//            }
//            if (biome.canHaveSnowfall()) {
//                if (oldBiomeStates.containsKey(biome.mId)) {
//                    this.mBiomeStates[biome.mId].snowLevel = oldBiomeStates.get(biome.mId).snowLevel;
//                } else {
//                    this.mBiomeStates[biome.mId].snowLevel = 0;
//                }
//            }
//        }
    }

    private void _enableBlockEntityAccessForThisThread() {
    }

    public final void onTickingStarted() {
        this._enableBlockEntityAccessForThisThread();
        this.mOwnedByTickingThread = true;
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
