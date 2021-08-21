package com.ddf.fakeplayer.level;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.ActorCategory;
import com.ddf.fakeplayer.actor.ActorUniqueID;
import com.ddf.fakeplayer.actor.IEntityRegistryOwner;
import com.ddf.fakeplayer.actor.definition.ActorDefinitionIdentifier;
import com.ddf.fakeplayer.actor.player.Abilities;
import com.ddf.fakeplayer.actor.player.LocalPlayer;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.block.BlockSourceListener;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.level.chunk.ChunkPos;
import com.ddf.fakeplayer.level.dimension.ChangeDimensionRequest;
import com.ddf.fakeplayer.level.dimension.Dimension;
import com.ddf.fakeplayer.network.PacketSender;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("all")
public abstract class Level implements BlockSourceListener/*, IWorldRegistriesProvider*/ {
    private List<Actor> entities = new ArrayList<>();

    private ItemRegistry itemRegistry;
    private ArrayList<ChunkPos> mTickingChunksOffset = new ArrayList<>();
    private ArrayList<ChunkPos> mClientTickingChunksOffset = new ArrayList<>();
    private ArrayList<Player> mPlayers = new ArrayList<>();
    private ArrayList<Player> mActivePlayers = new ArrayList<>();
    private ArrayList<Player> mSuspendedPlayers = new ArrayList<>();
    //private ArrayList<PlayerSuspension> mSuspendResumeList = new ArrayList<>();
    //private TagRegistry mTagRegistry = new TagRegistry();
    //private ActorInfoRegistry mActorInfoRegistry;
    //private SoundPlayer mSoundPlayer;
    private boolean mIsFindingSpawn = false;
    private boolean mSpawnEntities = true;
    private /*ActorUniqueID*/long mLastUniqueID = ActorUniqueID.INVALID_ID;
    private /*ActorRuntimeID*/long mLastRuntimeID = 0L;
    private HashMap</*ActorRuntimeID*/Long, /*ActorUniqueID*/Long> mRuntimeIdMap = new HashMap<>();
    private ArrayList<LevelListener> mListeners = new ArrayList<>();
    //private LevelStorage mLevelStorage;
    //public SavedDataStorage mSavedDataStorage;
    //private PhotoStorage mPhotoStorage;
    private LevelDataWrapper mLevelData = new LevelDataWrapper();
    //private Level.RegionSet mRegions = new HashSet<>();
    //private ActorDefinitionGroup mEntityDefinitions;
    //private ActorAnimationGroup mActorAnimationGroup;
    //private ActorAnimationControllerGroup mActorAnimationControllerGroup;
    //private BlockComponentFactory mBlockComponentFactory;
    //private BlockDefinitionGroup mBlockDefinitions;
    //private ActorSpawnRuleGroup mActorSpawnRules;
    //private SpawnGroupRegistry mSpawnGroupRegistry;
    //private Spawner mMobSpawner;
    //private ProjectileFactory mProjectileFactory;
    //private BehaviorFactory mBehaviorFactory = new BehaviorFactory();
    //private BehaviorTreeGroup mBehaviorTreeDefinitions;
    //private BlockPalette mGlobalBlockPalette;
    //private Recipes mRecipes;
    private HashMap<Integer, Dimension> mDimensions = new HashMap<>();
    //private PortalForcer mPortalForcer;
    //private Level.BossEventListenerList mBossEventListeners = new ArrayList<>();
    private HashMap<Player, ChangeDimensionRequest> mChangeDimensionRequests = new HashMap<>();
    //private PlayerListEntries mPlayerList = new HashMap<>();
    protected PacketSender mPacketSender = null;
    //private HitResult mHitResult = new HitResult();
    //private HitResult mLiquidHitResult = new HitResult();
    private Random mRandom = new Random();
    private Random mAnimateRandom = new Random();
    //private NetEventCallback mNetEventCallback = null;
    private ArrayList<Actor> mPendingEntitiesToRemove = new ArrayList<>(0);
    private boolean mUpdatingBlockEntities = false;
    private ArrayList<Actor> mGlobalEntities = new ArrayList<>(0);
    private ArrayList<Actor> mAutonomousEntities = new ArrayList<>(0);
    private ArrayList<Actor> mAutonomousLoadedEntities = new ArrayList<>();
    protected final boolean mIsClientSide;
    private boolean mIsExporting = false;
    //private ArrayList<PendingRemovalInfo> mPendingPlayerRemovals = new ArrayList<>();
    private long mLastTimePacketSent = 0L;
    private boolean mChunkSaveInProgress = false;
    private boolean mSimPaused = false;
    //private TaskGroup mMainThreadTaskGroup;
    //private TaskGroup mIOTaskGroup;
    //private Scheduler mScheduler;
    private String mLevelId = "";
    //private PriorityQueue<LevelChunkQueuedSavingElement> mLevelChunkSaveQueue;
    //private TickingAreasManager mTickingAreasMgr;
    //private HashSet<_TickPtr> mTempTickPointers = new HashSet<>();
    //private HashMap</*ActorUniqueID*/Long, MapItemSavedData> mMapData = new HashMap<>();
    private HashMap</*ActorUniqueID*/Long, Abilities> mAbilitiesData = new HashMap<>();
    private boolean mTearingDown = false;
    //private IMinecraftEventing mEventing;
    //private PlayerEventCoordinator mRemotePlayerEventCoordinator;
    //private ServerPlayerEventCoordinator mServerPlayerEventCoordinator;
    //private ClientPlayerEventCoordinator mClientPlayerEventCoordinator;
    //private ActorEventCoordinator mActorEventCoordinator;
    //private ClientLevelEventCoordinator mClientLevelEventCoordinator;
    //private ServerLevelEventCoordinator mServerLevelEventCoordinator;
    //private BlockEventCoordinator mBlockEventCoordinator;
    //private ItemEventCoordinator mItemEventCoordinator;
    //private StructureManager mStructureManager = structureManager;
    private /*ActorUniqueID*/long mParentMapId = ActorUniqueID.INVALID_ID;
    protected boolean mIsFinishedInitializing = false;
    private long mNextSaveDataTime = Long.MAX_VALUE;
    private long mNextStorageCheckTime = Long.MIN_VALUE;
    private boolean mStorageActionsDeferred = false;
    //private LootTables mLootTables = new LootTables();
    private /*uint32_t*/long mMobTickCount = 0;
    private /*uint32_t*/long mMobTickCountPrevious = 0;
    //private Scoreboard mScoreboard;
    private BlockLegacy mRegisteredBorderBlock = null;
    //private ActorFactory mActorFactory;
    //private IEntityRegistryOwner mEntityRegistryOwner;
    //private EntitySystems mEntitySystems = new EntitySystems();
    //private HashMap</*uint32_t EntityNetId*/Long, EntityOptionalOwnerRef> mSimulatedEntities = new HashMap<>();
    //private FeatureRegistry mFeatureRegistry;
    //private FeatureTypeFactory mFeatureTypeFactory;
    //private JigsawStructureRegistry mJigsawStructureRegistry = new JigsawStructureRegistry();
    //private BiomeRegistry mBiomes;
    //private BiomeComponentFactory mBiomeComponentFactory;
    //private SurfaceBuilderRegistry mSurfaceBuilders;
    //private Factory<Dimension, Level, Scheduler> mDimensionFactory = new Factory<>();
    //private WireframeQueue mWireframeQueue = new WireframeQueue();
    //private BlockActorLevelListener mBlockActorLevelListener = new BlockActorLevelListener();
    private boolean mServerAuthoritativeMovement = true;

    private boolean mShouldCorrectPlayerMovement = false;
    private float mPlayerMovementScoreThreshold = 20.0f;
    private float mPlayerMovementDistanceThreshold = 0.30000001f;
    private float mPlayerMovementDistanceThresholdSqr = this.mPlayerMovementDistanceThreshold * this.mPlayerMovementDistanceThreshold;

    @NotImplemented
    protected Level(ItemRegistry itemRegistry, /*SoundPlayer*/Object soundPlayer, /*LevelStorage*/Object levelStorage, /*IMinecraftEventing*/Object eventing, boolean isClientSide, /*Scheduler*/Object callbackContext, /*StructureManager*/Object structureManager, /*ResourcePackManager*/Object addOnResourcePackManager, IEntityRegistryOwner entityRegistryOwner, /*BlockComponentFactory*/Object blockComponentFactory, /*BlockDefinitionGroup*/Object blockDefinitionGroup) {
        this.itemRegistry = itemRegistry;
//        this.mSoundPlayer = soundPlayer;
//        this.mLevelStorage = levelStorage
//        this.mEventing = eventing;
//        this.mSavedDataStorage = new SavedDataStorage(levelStorage);
//        this.mBlockComponentFactory = blockComponentFactory;
//        this.mBlockDefinitions = blockDefinitionGroup;
//        this.mBehaviorTreeDefinitions = new BehaviorTreeGroup(addOnResourcePackManager, this.mBehaviorFactory);
//        this.mGlobalBlockPalette = new BlockPalette(this);
//        this.mPortalForcer = new PortalForcer(this);
        this.mIsClientSide = isClientSide;
//        this.mScheduler = callbackContext;
//        this.mTickingAreasMgr = new TickingAreasManager(this.mDimensions);
//        this.mActorFactory = new ActorFactory(this);
//        this.mEntityRegistryOwner = entityRegistryOwner;
    }

    public static boolean isUsableLevel(Level level) {
        if ( level != null)
            return  !level.getTearingDown();
        return false;
    }

    public Actor fetchEntity(/*ActorUniqueID*/long actorId, boolean getRemoved) {
        return getEntityByUniqueId(actorId);
    }

    public boolean isClientSide() {
        return this.mIsClientSide;
    }

    public final boolean hasStartWithMapEnabled() {
        return this.mLevelData.get().hasStartWithMapEnabled();
    }

    public final AdventureSettings getAdventureSettings() {
        return this.mLevelData.get().getAdventureSettings();
    }

    public final long getCurrentTick() {
        return this.mLevelData.get().getCurrentTick();
    }

    public Difficulty getDifficulty() {
        return this.mLevelData.get().getGameDifficulty();
    }

    public final Abilities getDefaultAbilities() {
        return this.mLevelData.get().getDefaultAbilities();
    }

    public final GameType getDefaultGameType() {
        return this.mLevelData.get().getGameType();
    }

    public final BlockPos getDefaultSpawn() {
        return this.mLevelData.get().getSpawnPos();
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public LevelData getLevelData() {
        return this.mLevelData.get();
    }

    public final /*ActorUniqueID*/long getNewUniqueID() {
        return this.mLastUniqueID++;
    }

    public PacketSender getPacketSender() {
        return this.mPacketSender;
    }

    public Player getPlayer(/*ActorUniqueID*/long entityID) {
        Actor e = this.fetchEntity(entityID, false);
        if (e != null && e.hasCategory(ActorCategory.Player_1))
            return (Player) e;
        else
            return null;
    }

    public Player getPrimaryLocalPlayer() {
        return this.findPlayer(Player::isLocalPlayer);
    }

    public final boolean getTearingDown() {
        return this.mTearingDown;
    }

    public final void setDefaultGameType(GameType gameType) {
        this.mLevelData.get().setGameType(gameType);
    }

    public final void setDefaultSpawn(final BlockPos spawnPos) {
        this.mLevelData.get().setSpawnPos(spawnPos);
        this.saveLevelData();
    }

    public final void setLevelId(String levelId) {
        this.mLevelId = levelId;
    }

    public final void setTime(int time) {
        this.mLevelData.get().setTime(time);
    }

    public void setFinishedInitializing() {
        this.mIsFinishedInitializing = true;
    }

    @NotImplemented
    public final void saveLevelData() {
    }

    public void forEachPlayer(Function<Player, Boolean> function) {
        for (Player player : this.mActivePlayers) {
            if (!player.isRemoved() && !function.apply(player)) {
                break;
            }
        }
    }

    public final Player findPlayer(Function<Player, Boolean> function) {
        for (Player player : this.mActivePlayers) {
            if (function.apply(player)) {
                return player;
            }
        }
        return null;
    }

    @NotImplemented
    public final void broadcastLevelEvent(LevelEvent type, final Vec3 pos, int data, Player p) {
    }

    public final void playSound(LevelSoundEvent type, final Vec3 pos, int data, final ActorDefinitionIdentifier entityType, boolean isBabyMob, boolean isGlobal) {
        for (LevelListener listener : this.mListeners) {
            listener.levelSoundEvent(type, pos, data, entityType, isBabyMob, isGlobal);
        }
    }

    public final void denyEffect(final Vec3 pos) {
        this.broadcastLevelEvent(LevelEvent.ParticleDenyBlock, pos, 0, null);
        this.playSound(LevelSoundEvent.Deny, pos, -1, new ActorDefinitionIdentifier(), false, false);
    }

    @NotImplemented
    public void tick() {
        this.mMobTickCountPrevious = this.mMobTickCount;
        this.mMobTickCount = 0;
        this.tickEntities();
//        this.getLevelEventCoordinator().sendLevelTick();
        if (this.getCurrentTick() % 0x14 == 0) {
            Collections.shuffle(this.mTickingChunksOffset);
            Collections.shuffle(this.mClientTickingChunksOffset);
            Collections.shuffle(this.mActivePlayers);
        }
        //this.mTickingAreasMgr.update(this);
        this.mLevelData.get().incrementTick();
        //this.mTickingAreasMgr.tick(this.mLevelData.get().getCurrentTick());

        this.forEachPlayer(player -> {
            if (player.isSpawned())
                player.moveView();
            return true;
        });
//        this.forEachPlayer(player -> {
//            player.tickWorld(this.mLevelData.get().getCurrentTick());
//            return true;
//        });
        getPrimaryLocalPlayer().tick(null);
//        this.tickEntitySystems();
//        this._saveSomeChunks();
//        this._handlePlayerSuspension();
        this._cleanupDisconnectedPlayers();
        this._handleChangeDimensionRequests();
    }

    @NotImplemented
    public void tickEntities() {
//        this._tickTemporaryPointers();
        this.mGlobalEntities.removeIf(entity -> {
            if (entity.isRemoved()) {
                return true;
            } else {
                entity.setPosPrev(entity.getStateVectorComponent().getPos());
                entity.mRotPrev = entity.mRot;
                //entity.setRegion(this.getDimension(entity.getDimensionId()).getBlockSourceDEPRECATEDUSEPLAYERREGIONINSTEAD());
                entity.tick(entity.getRegion());
                return false;
            }
        });
        this.mAutonomousEntities.removeIf(Actor::isRemoved);
    }

    public final void tickedMob() {
        ++this.mMobTickCount;
    }

    @NotImplemented
    public void _cleanupDisconnectedPlayers() {
        Iterator<Player> iterator = this.mPlayers.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (!player.isRemoved()) {
                continue;
            }
            if (player.isRiding()) {
                player.stopRiding(true, true, false);
            }
//            if (this.mHitResult.getEntity() == player) {
//                this.mHitResult.resetHitType();
//                this.mHitResult.resetHitEntity();
//            }
            if (this.mActivePlayers.contains(player))
                this.mActivePlayers.remove(player);
            else if (this.mSuspendedPlayers.contains(player))
                this.mSuspendedPlayers.remove(player);
            iterator.remove();
            this.updateSleepingPlayerList();
        }
    }

    public void updateSleepingPlayerList() {
    }

    @NotImplemented
    private void _handleChangeDimensionRequests() {
        if (this.mChangeDimensionRequests.size() > 0 && this.mIsFinishedInitializing) {
            ArrayList<Player> toRemove = new ArrayList<>();
            for (Map.Entry<Player, ChangeDimensionRequest> pair : this.mChangeDimensionRequests.entrySet()) {
                Player player = pair.getKey();
                ChangeDimensionRequest changeRequest = pair.getValue();
                if (changeRequest.mToDimensionId != changeRequest.mFromDimensionId && this.getPlayer(player.getUniqueID()) != null) {
//                    long destinationDimension = changeRequest.mToDimensionId;
//                    player.fireDimensionChangedEvent(destinationDimension);
                    if (this._playerChangeDimension(player, changeRequest))
                        toRemove.add(player);
                }
//                else if (!this.isPlayerSuspended(player)) {
//                    toRemove.add(player);
//                }
            }
            for (Player player : toRemove) {
                this.mChangeDimensionRequests.remove(player);
            }
        }
    }

    @NotImplemented
    private boolean _playerChangeDimension(Player player, ChangeDimensionRequest changeRequest) {
        long fromId = changeRequest.mFromDimensionId;
        long toId = changeRequest.mToDimensionId;
        if (changeRequest.mState == ChangeDimensionRequest.State.PrepareRegion) {
            long id = toId;
//            Dimension toDimension = this.createDimension(toId);
            player.mDimensionState = Player.DimensionState.WaitingArea;
            if (this.mIsClientSide) {
                for (Player p : this.mActivePlayers) {
                    if (!p.isLocalPlayer())
                        p.destroyRegion();
                }
            }
            player.suspendRegion();
            player._fireWillChangeDimension();
            player.destroyRegion();
            player.setPos(changeRequest.mPosition);
//            player.lerpTo(Vec3.ZERO, Vec2.ZERO, 0);
//            player.prepareRegion(toDimension.getChunkSource());
            player._fireDimensionChanged();
            player._setDimensionId(changeRequest.mToDimensionId);
            changeRequest.mState = ChangeDimensionRequest.State.WaitingForChunks;
        }
//        if (changeRequest.mState == ChangeDimensionRequest.State.WaitingForChunks) {
//            BlockPos min = new BlockPos(player.getPos().subtract(16.0f));
//            BlockPos max = new BlockPos(player.getPos().add(16.0f));
//            if (!player.getRegion().hasChunksAt(min, max)){
//                return false;
//            }
//        }
        if (changeRequest.mRespawn) {
            if (changeRequest.mState == ChangeDimensionRequest.State.WaitingForChunks)
                changeRequest.mState = ChangeDimensionRequest.State.WaitingForRespawn;
            if (changeRequest.mState == ChangeDimensionRequest.State.WaitingForRespawn && player.isRespawnReady()) {
                player.mDimensionState = Player.DimensionState.Ready_0;
                player.respawn();
                return true;
            } else {
                return false;
            }
        }
        if (this.mIsClientSide) {
            long runtimeId = player.getRuntimeID();
            PlayerActionPacket changeDimensionPacketAck = new PlayerActionPacket();
            changeDimensionPacketAck.setAction(PlayerActionType.DIMENSION_CHANGE_SUCCESS); //ChangeDimensionAck
            changeDimensionPacketAck.setRuntimeEntityId(runtimeId);
            changeDimensionPacketAck.setFace(0);
            changeDimensionPacketAck.setBlockPosition(Vector3i.ZERO);
            this.mPacketSender.send(changeDimensionPacketAck);
        }
        player.mDimensionState = Player.DimensionState.Ready_0;
        return true;
    }

    public final void requestPlayerChangeDimension(Player player, ChangeDimensionRequest changeRequest) {
        if (!this.mChangeDimensionRequests.containsKey(player)) {
            player.mDimensionState = Player.DimensionState.Pending;
            player.lerpMotion(new Vec3(0.0f, 0.0f, 0.0f));
            this.mChangeDimensionRequests.put(player, changeRequest);
        }
        this._handleChangeDimensionRequests();
    }

//----------------------------------------------------------------------------------------------------------------------

    public void addEntity(Actor actor) {
        entities.add(actor);
        if (actor instanceof Player) {
            this.mPlayers.add((Player) actor);
        }
        if (actor instanceof LocalPlayer) {
            this.mActivePlayers.add((Player) actor);
        }
    }

    public Actor getEntityByRuntimeId(long runtimeEntityId) {
        for (Actor actor : entities) {
            if (actor.getRuntimeID() == runtimeEntityId) {
                return actor;
            }
        }
        return null;
    }

    public Actor getEntityByUniqueId(long uniqueEntityId) {
        for (Actor actor : entities) {
            if (actor.getUniqueID() == uniqueEntityId) {
                return actor;
            }
        }
        return null;
    }

    public Player getPlayerByName(String name) {
        for (Player player : this.mPlayers) {
            if (player.getName().equals(name))
                return player;
        }
        return null;
    }

    public void removeEntity(long uniqueEntityId) {
        Actor actor = getEntityByUniqueId(uniqueEntityId);
        if (actor == null) {
            return;
        }
        entities.remove(actor);
        if (actor instanceof Player) {
            this.mPlayers.remove(actor);
        }
    }

    public final void setServerAuthoritativeMovement(boolean serverAuthoritativeMovement) {
        this.mServerAuthoritativeMovement = serverAuthoritativeMovement;
    }

    public final boolean isServerAuthoritativeMovement() {
        return this.mServerAuthoritativeMovement;
    }
}
