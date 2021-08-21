package com.ddf.fakeplayer.level.dimension;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.block.BlockSource;
import com.ddf.fakeplayer.level.SavedData;
import com.ddf.fakeplayer.level.chunk.*;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.level.LevelListener;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.network.NetworkIdentifierWithSubId;
import com.ddf.fakeplayer.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

@SuppressWarnings("all")
public abstract class Dimension extends SavedData implements LevelListener  {
    private final int CIRCUIT_TICK_RATE = 2;

    private Level mLevel;
    private /*int16_t Height*/short mSeaLevel = 0;
    private BlockSource mBlockSource;
    private float[] mMobsPerChunkSurface = new float[7];
    private float[] mMobsPerChunkUnderground = new float[7];
    private BrightnessPair mDefaultBrightness;
    private String mName;
    private /*DimensionType*/int mId;
    private boolean mUltraWarm = false;
    private boolean mHasCeiling = false;
    private boolean mHasWeather = false;
    private boolean mHasSkylight = true;
    private /*Brightness*/int mSkyDarken = Brightness.MIN;
    private /*int16_t Height*/short mHeight;
    private float[] mBrightnessRamp = new float[16];
    //private BlockEventDispatcher mDispatcher = new BlockEventDispatcher();
    //private TaskGroup mTaskGroup;
    //private PostprocessingManager mPostProcessingManager = new PostprocessingManager();
    private ChunkSource mChunkSource;
    //private WorldGenerator mWorldGenerator = null;
    private Weather mWeather;
    private Seasons mSeasons;
    //private CircuitSystem mCircuitSystem = new CircuitSystem();
    private int mCircuitSystemTickRate = this.CIRCUIT_TICK_RATE;
    private HashMap</*ActorUniqueID*/Long, Actor> mEntityIdLookup = new HashMap<>();
    private HashMap<ChunkPos, ArrayList<CompoundTag>> mLimboEntities = new HashMap<>();
    private ArrayList<Actor> mEntitiesToMoveChunks = new ArrayList<>();
    //private TickingAreaList mTickingAreaList = new TickingAreaList();
    //private LevelChunkGarbageCollector mLevelChunkGarbageCollector = new LevelChunkGarbageCollector(this);
    private TreeSet</*ActorUniqueID*/Long> mWitherIDs = new TreeSet<>();
    //private RuntimeLightingManager mRuntimeLightingManager;
    //private LevelChunkBuilderData mLevelChunkBuilderData = new LevelChunkBuilderData();
    private long mLastPruneTime = 0;
    //private ChunkBuildOrderPolicyBase mChunkBuildOrderPolicy = new ChunkBuildOrderPolicy();
    //private VillageManager mVillageManager;
    ArrayList<NetworkIdentifierWithSubId> mTemporaryPlayerIds = new ArrayList<>();

    protected Dimension(Level level, /*DimensionType*/int dimId, /*int16_t Height*/short height, /*Scheduler*/Object callbackContext, String name) {
        super(name);
        this.mLevel = level;
        /*unsigned char Brightness*/int sky = Brightness.MAX;
        /*unsigned char Brightness*/int block = Brightness.MIN;
        this.mDefaultBrightness = new BrightnessPair(sky, block);
        this.mName = name;
        this.mId = dimId;
        this.mHeight = height;
//        this.mTaskGroup = new TaskGroup(MinecraftWorkerPool.ASYNC, callbackContext, "A dimension task group");
//        this.mLevel.addListener(this);
//        if (dimId != VanillaDimensions.TheEnd){
//            if (!this.mLevel.mSavedDataStorage.loadAndSet(this, this.getName())) {
//                this.mLevel.mSavedDataStorage.set(this.getName(), this);
//            }
//        }
        this.mWeather = new Weather(this);
        this.mSeasons = new Seasons(this);
//        this.mMobsPerChunkSurface[MobSpawnInfo.categoryID(ActorType.Animal)] = 4.0f;
//        this.mMobsPerChunkSurface[MobSpawnInfo.categoryID(ActorType.WaterAnimal)] = 36.0f;
//        this.mMobsPerChunkSurface[MobSpawnInfo.categoryID(ActorType.Monster)] = 8.0f;
//        this.mMobsPerChunkSurface[MobSpawnInfo.categoryID(ActorType.Ambient)] = 0.0f;
//        this.mMobsPerChunkSurface[MobSpawnInfo.categoryID(ActorType.Cat)] = 4.0f;
//        this.mMobsPerChunkSurface[MobSpawnInfo.categoryID(ActorType.Pillager)] = 8.0f;
//        this.mMobsPerChunkUnderground[MobSpawnInfo.categoryID(ActorType.Animal)] = 0.0f;
//        this.mMobsPerChunkUnderground[MobSpawnInfo.categoryID(ActorType.WaterAnimal)] = 0.0f;
//        this.mMobsPerChunkUnderground[MobSpawnInfo.categoryID(ActorType.Monster)] = 8.0f;
//        this.mMobsPerChunkUnderground[MobSpawnInfo.categoryID(ActorType.Ambient)] = 2.0f;
//        this.mMobsPerChunkUnderground[MobSpawnInfo.categoryID(ActorType.Cat)] = 0.0f;
//        this.mMobsPerChunkUnderground[MobSpawnInfo.categoryID(ActorType.Pillager)] = 8.0f;
//        if (!this.mLevel.isClientSide()) {
//            this.mVillageManager = new VillageManager(this);
//        }
        this.setDirty(true);
    }

    public int getDefaultBiome() {
        return 0;
    }

    public /*int16_t Height*/short getHeight() {
        return this.mHeight;
    }

    public Level getLevel() {
        return this.mLevel;
    }

    public BrightnessPair getDefaultBrightness() {
        return new BrightnessPair(this.mDefaultBrightness);
    }

    public int getId() {
        return this.mId;
    }
}
