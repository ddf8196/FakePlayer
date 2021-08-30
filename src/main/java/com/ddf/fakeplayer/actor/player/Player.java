package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.actor.*;
import com.ddf.fakeplayer.actor.attribute.*;
import com.ddf.fakeplayer.actor.mob.Mob;
import com.ddf.fakeplayer.container.inventory.PlayerInventoryProxy;
import com.ddf.fakeplayer.level.chunk.ChunkSource;
import com.ddf.fakeplayer.level.dimension.VanillaDimensions;
import com.ddf.fakeplayer.level.gamemode.GameMode;
import com.ddf.fakeplayer.level.gamemode.SurvivalMode;
import com.ddf.fakeplayer.actor.player.permission.CommandPermissionLevel;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.block.Material;
import com.ddf.fakeplayer.container.Container;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.EnderChestContainer;
import com.ddf.fakeplayer.container.hud.HudContainerManagerModel;
import com.ddf.fakeplayer.container.inventory.Inventory;
import com.ddf.fakeplayer.container.inventory.InventoryAction;
import com.ddf.fakeplayer.container.inventory.InventorySource;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.InventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.InventoryTransactionManager;
import com.ddf.fakeplayer.item.*;
import com.ddf.fakeplayer.level.Difficulty;
import com.ddf.fakeplayer.level.GameType;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.network.NetworkIdentifier;
import com.ddf.fakeplayer.network.PacketSender;
import com.ddf.fakeplayer.util.ColorFormat;
import com.ddf.fakeplayer.util.DataConverter;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.data.entity.EntityEventType;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;

import java.util.*;

@SuppressWarnings("all")
public abstract class Player extends Mob {
    public static final String HUNGER = "minecraft:player.hunger";
    public static final String EXHAUSTION = "minecraft:player.exhaustion";
    public static final String SATURATION = "minecraft:player.saturation";
    public static final String EXPERIENCE = "minecraft:player.experience";
    public static final String LEVEL = "minecraft:player.level";

    private AuthoritativeMovementMode movementMode;

    private int mCastawayTimer = 0;
    private boolean mAteKelp = false;
    private int mLastBiome = 0;
    private ArrayList<Integer> mOceanBiomes = new ArrayList<>();
    private boolean castawaySent = false;
    private boolean sailseasSent = false;
    public Player.DimensionState mDimensionState = DimensionState.Ready_0;
    private boolean mServerHasMovementAuthority = false;
    private char mUserType = 0;
    private int mScore = 0;
    private float mOBob = 0.0f;
    private float mBob = 0.0f;
    private boolean mHandsBusy = false;
    private String mName;
    //private BuildPlatform mBuildPlatform = Unknown_13;
    public Abilities mAbilities;
    private NetworkIdentifier mOwner;
    private String mUniqueName;
    private String mServerId;
    private String mSelfSignedId;
    private String mPlatformOfflineId;
    private /*uint64_t*/long mClientRandomId = 0L;
    private UUID mClientUUID;
    //private Certificate mCertificate;
    String mPlatformId;
    private /*ActorUniqueID*/long mPendingRideID = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mPendingLeftShoulderRiderID = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mPendingRightShoulderRiderID = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mInteractTarget = ActorUniqueID.INVALID_ID;
    private Vec3 mInteractTargetPos = new Vec3(Vec3.ZERO);
    private boolean mHasFakeInventory = false;
    private boolean mIsRegionSuspended = false;
    //private ChunkViewSource mChunkSource;
    //private ChunkViewSource mSpawnChunkSource;
    //private BlockSource mOwnedBlockSource;
    private BlockPos mBedPosition = new BlockPos();
    private Vec3 mTeleportDestPos = new Vec3();
    private boolean mUpdateMobs = true;
    private Vec3 mFirstPersonLatestHandOffset = new Vec3();
    private Vec3 mCapePosO = new Vec3();
    private Vec3 mCapePos = new Vec3();
    private float[] mPaddleForces = new float[2];
    private boolean[] mIsPaddling = new boolean[2];
    private float mDistanceSinceTravelledEvent = 0.0f;
    private float mDistanceSinceTransformEvent = 0.0f;
    //private IContainerManager mContainerManager;
    private PlayerInventoryProxy mInventoryProxy;
    //private SerializedSkin mSkin = new SerializedSkin();
    private ArrayList<ItemInstance> mCreativeItemList = new ArrayList<>();
    private /*SubClientId*/byte mClientSubId;
    private String mPlatformOnlineId;
    private Player.SpawnPositionState mSpawnPositionState = SpawnPositionState.InitializeSpawnPositionRandomizer;
    private Player.SpawnPositionSource mSpawnPositionSource = SpawnPositionSource.Randomizer;
    private Vec3 mSpawnPositioningTestPosition = new Vec3();
    private boolean mBlockRespawnUntilClientMessage = false;
    private /*uint32_t*/long mRespawnChunkBuilderPolicyHandle = -1;
    //private Player.CachedSpawnData mCachedSpawnData = new Player.CachedSpawnData();
    //private BlockSource mSpawnBlockSource;
    private boolean mHasSeenCredits = false;
    //private Stopwatch mRespawnStopwatch_Searching = new Stopwatch();
    private Vec3 mRespawnOriginalPosition = new Vec3();
    private /*DimensionType*/int mRespawnOriginalDimension = 0;
    protected boolean mRespawnReady = false;
    private boolean mRespawnBlocked = false;
    private /*DimensionType*/int mRespawnDimensionId = VanillaDimensions.Undefined;
    private boolean mCheckBed = false;
    private boolean mIsInitialSpawnDone = false;
    private boolean mPositionLoadedFromSave = false;
    private int mFixStartSpawnFailures = 0;
    private ItemStack mItemInUse = new ItemStack(getLevel().getItemRegistry());
    private PlayerInventoryProxy.SlotData mItemInUseSlot = new PlayerInventoryProxy.SlotData(-1, ContainerID.CONTAINER_ID_NONE);
    protected int mItemInUseDuration = 0;
    private short mSleepCounter = 0;
    private short mPrevSleepCounter = 0;
    private boolean mInteractDataDirty = true;
    private /*ActorUniqueID*/long mPreviousInteractEntity = ActorUniqueID.INVALID_ID;
    private int mPreviousCarriedItem = 0;
    private boolean mAutoJumping = false;
    private int mEmoteTicks = 0;
    protected PacketSender mPacketSender;
    private BlockPos mBounceStartPos = new BlockPos();
    private Block mBounceBlock = null;
    private float mFOVModifier = 1.0f;
    private HudContainerManagerModel mHudContainerManagerModel;
    private EnderChestContainer mEnderChestInventory = new EnderChestContainer();
    private ArrayList</*ActorUniqueID*/Long> mTrackedBossIDs = new ArrayList<>();
    private Player.PositionMode mPositionMode = PositionMode.Normal_4;
    private ActorType mLastHurtBy = ActorType.Undefined_2;
    //private ItemGroup mCursorSelectedItemGroup = new ItemGroup();
    //private PlayerUIContainer mPlayerUIContainer;
    private InventoryTransactionManager mTransactionManager;
    private GameMode mGameMode;
    //private PlayerRespawnRandomizer mSpawnRandomizer = new PlayerRespawnRandomizer();
    private float mVRMoveAdjAngle = 0.0f;
    private boolean mUseUIAnimationComponent = false;
    //private AnimationComponent mUIAnimationComponent;
    //Player.PlayerListenerList mListeners = new ArrayList<>();
    private int mLastLevelUpTime;
    private boolean mPlayerLevelChanged;
    private int mPreviousLevelRequirement;
    private BlockPos mRespawnPosition = new BlockPos(0, -1, 0);
    private boolean mIsForcedRespawnPos = false;
    private boolean mPlayerIsSleeping = false;
    private boolean mAllPlayersSleeping = false;
    private boolean mDestroyingBlock = false;
    private Vec3 mSurvivalViewerPosition = new Vec3();
    private ArrayList</*unsigned int*/Long> mOnScreenAnimationTextures = new ArrayList<>();
    private int mOnScreenAnimationTicks = 0;
    private GameType mPlayerGameType;
    private int mEnchantmentSeed;
    private /*uint32_t*/long mChunkRadius = 6;
    private int mMapIndex = 1;
    //private LoopingSoundHandle mElytraLoop = -1L;
    private float mUnderwaterLightLevel = 15.0f;
    private ArrayList<Integer> mCooldowns = new ArrayList<>(CooldownType.Count_24.getValue());
    private long mStartedBlockingTimeStamp = 0L;
    private long mBlockedUsingShieldTimeStamp = 0L;
    private long mBlockedUsingDamagedShieldTimeStamp = 0L;
    private boolean mPrevBlockedUsingShield = false;
    private boolean mPrevBlockedUsingDamagedShield = false;
    private boolean mUsedPotion = false;
    private int mBounceHeight = 0;
    //private SkinAdjustments mSkinAdjustments = new SkinAdjustments();
    //private SerializedSkin mSerializedSkin = new SerializedSkin();
    private int mScanForDolphinTimer = 60;
    private boolean mR5DataRecoverComplete = false;
    private String mDeviceId;
    private boolean mFlagClientForBAIReset = false;
    //private BedHelper mBedHelper = new BedHelper(270, 180, 90, 0, 1.8, 1.8, 0.1, 0.89999998, 0.89999998, 0.1);

    protected Player(Level level, PacketSender packetSender, GameType playerGameType, final NetworkIdentifier owner, /*SubClientId*/byte subid, UUID uuid, final String deviceId, /*Certificate*/Object certificate, final String platformId, final String platformOnlineId) {
        super(level);
        this.mAbilities = new Abilities(level.getDefaultAbilities());
        this.mClientUUID = uuid;
//        this.mCertificate = certificate;
        this.mPlatformId = platformId;
        this.mClientSubId = subid;
        this.mPlatformOnlineId = platformOnlineId;
        this.mPacketSender = packetSender;
//        String name = "";
//        int size = PlayerUISlot._count_17.getValue();
//        this.mPlayerUIContainer = new PlayerUIContainer(name, 0, size);
        this.mTransactionManager = new InventoryTransactionManager(this);
        this.mPlayerGameType = playerGameType;
        for (int i = 0; i < CooldownType.Count_24.getValue(); i++) {
            this.mCooldowns.add(0);
        }
        this.mDeviceId = deviceId;
        this.mCategories |= ActorCategory.Player_1.getValue();
//        this.setActorRendererId(VanillaActorRendererId.player);
//        registry = super.getLevel().getEntityRegistryOwner().getEntityRegistry();
//        if (registry != null) {
//            super.initEntity(registry);
//        }
        this.registerAttributes();
        this.mEntityData.define(ActorDataIDs.PLAYER_FLAGS.ordinal(), (byte) 0);
        this.mEntityData.define(ActorDataIDs.PLAYER_INDEX.ordinal(), 0);
        this.mEntityData.define(ActorDataIDs.BED_POSITION.ordinal(), new BlockPos());
        this.mEntityData.define(ActorDataIDs.INTERACT_TEXT.ordinal(), "");
        this.mEntityData.define(ActorDataIDs.SCORE.ordinal(), "");
        this.mEntityData.define(ActorDataIDs.AGENT.ordinal(), ActorUniqueID.INVALID_ID);
        super._setHeightOffset(1.62001f);
        this.setPlayerGameType(this.mPlayerGameType);
        Inventory inventory = new Inventory(this);
        this.mInventoryProxy = new PlayerInventoryProxy(getLevel().getItemRegistry(), inventory);
        this.mHudContainerManagerModel = new HudContainerManagerModel(ContainerID.CONTAINER_ID_NONE, this);
        this.mInventoryProxy.init(this.mHudContainerManagerModel);
//        this._registerElytraLoopSound();
        this.mOnGround = false;
//        this.mSkin.setIsTrustedSkin(false);
    }

    public final boolean canDestroy(final Block block) {
        Material Material = block.getMaterial();
        if (Material.isAlwaysDestroyable())
            return true;
        ItemStack SelectedItem = this.getSelectedItem();
        return SelectedItem.canDestroySpecial(block);
    }

    public final boolean canUseAbility(AbilitiesIndex abilityIndex) {
        Ability ability = this.mAbilities.getAbility(abilityIndex);
        if (ability.getBool()) {
            return true;
        }
        if (ability.hasOption(Ability.Options.WorldbuilderOverrides)) {
            return this.mAbilities.getAbility(AbilitiesIndex.WorldBuilder).getBool();
        }
        return false;
    }

    public final boolean canUseOperatorBlocks() {
        if (this.mAbilities.getBool(AbilitiesIndex.Instabuild) && this.getCommandPermissionLevel() != CommandPermissionLevel.Any) {
            return this.canUseAbility(AbilitiesIndex.OperatorCommands);
        }
        return false;
    }

    public int getItemCooldownLeft(CooldownType type) {
        if (type == CooldownType.TypeNone)
            return 0;
        return this.mCooldowns.get(type.getValue());
    }

    public final GameMode getGameMode() {
        return this.mGameMode;
    }

//    public IMinecraftEventing getEventing() {
//        return this._getLevelPtr().getEventing();
//    }

    public boolean isHungry() {
        AttributeInstance hunger = this.getAttribute(Player.HUNGER);
        return hunger.getMaxValue() > hunger.getCurrentValue();
    }

    public final boolean isUsingItem() {
        return !this.mItemInUse.isNull();
    }

    @Override
    public final boolean isCreative() {
        if (this.mPlayerGameType == GameType.Creative || this.mPlayerGameType == GameType.CreativeViewer) {
            return true;
        }
        if (this.mPlayerGameType == GameType.Default) {
            return this.getLevel().getDefaultGameType() == GameType.Creative;
        }
        return false;
    }

    public final boolean isSurvival() {
        if (this.mPlayerGameType == GameType.Survival || this.mPlayerGameType == GameType.SurvivalViewer || this.mPlayerGameType == GameType.Adventure ) {
            return true;
        }
        if (this.mPlayerGameType == GameType.Default) {
            return this.getLevel().getDefaultGameType() == GameType.Adventure || this.getLevel().getDefaultGameType() == GameType.Survival;
        }
        return false;
    }

    public final boolean isInCreativeMode() {
        if (this.mPlayerGameType == GameType.Creative) {
            return true;
        } else if (this.mPlayerGameType == GameType.Default){
            return this.getLevel().getDefaultGameType() == GameType.Creative;
        }
        return false;
    }

    public boolean isLocalPlayer() {
        return false;
    }

    public boolean isHostingPlayer() {
        return false;
    }

    public boolean isLoading() {
        return false;
    }

    public final boolean isSpawned() {
        return this.mIsInitialSpawnDone;
    }

    public boolean isItemInCooldown(CooldownType type) {
        return this.getItemCooldownLeft(type) > 0;
    }

    @Override
    public boolean isSleeping() {
        return this.mPlayerIsSleeping;
    }

    public final boolean isRespawnReady() {
        return this.mRespawnReady;
    }

    public void startUsingItem(final ItemStack instance, int duration) {
        boolean v10 = false;
        if (instance.equals(this.mItemInUse) ) {
            if (this.mItemInUseSlot.mSlot == this.mInventoryProxy.getSelectedSlot().mSlot) {
                v10 = this.mItemInUseSlot.mContainerId == this.mInventoryProxy.getSelectedSlot().mContainerId;
            }
        }
        if (!v10) {
            this.mItemInUse = new ItemStack(instance);
            this.mItemInUseSlot.mSlot = this.mInventoryProxy.getSelectedSlot().mSlot;
            this.mItemInUseSlot.mContainerId = this.mInventoryProxy.getSelectedSlot().mContainerId;
            this.mItemInUseDuration = duration;
            if (!this.getLevel().isClientSide())
                this.setStatusFlag(ActorFlags.USINGITEM, true);
        }
    }

    public final void stopUsingItem() {
        this.mItemInUse.setNull();
        this.mItemInUseSlot.mSlot = -1;
        this.mItemInUseSlot.mContainerId = ContainerID.CONTAINER_ID_NONE;
        this.mItemInUseDuration = 0;
        if (!this.getLevel().isClientSide())
            this.setStatusFlag(ActorFlags.USINGITEM, false);
    }

    @NotImplemented
    public final void releaseUsingItem() {
        if (!this.mItemInUse.isNull()) {
            this.mItemInUse.releaseUsing(this, this.mItemInUseDuration);
            ItemStack item = new ItemStack(this.getCarriedItem());
            if (item.toBoolean() && !this.mItemInUse.matchesItem(item))
                this.setSelectedItem(this.mItemInUse);
        }
        this.stopUsingItem();
    }

    @NotImplemented
    public final void completeUsingItem() {
        this.stopUsingItem();
    }

    public final InventoryTransactionManager getTransactionManager() {
        return this.mTransactionManager;
    }

    public ItemStack getCarriedItem() {
        return this.getSelectedItem();
    }

    @Override
    public ActorType getEntityTypeId() {
        return ActorType.Player_0;
    }

    public final GameType getPlayerGameType() {
        if (this.mPlayerGameType != GameType.Default)
            return this.mPlayerGameType;
        return this.getLevel().getDefaultGameType();
    }

    public ItemStack getSelectedItem() {
        return this.mInventoryProxy.getSelectedItem();
    }

    public final int getSelectedItemSlot() {
        return this.mInventoryProxy.getSelectedSlot().mSlot;
    }

    public int getItemUseDuration() {
        return this.mItemInUseDuration;
    }

    public final PlayerInventoryProxy getSupplies() {
        return this.mInventoryProxy;
    }

    public final float getDestroyProgress(final Block block) {
        if (block.getDestroySpeed() < 0.0f )
            return 0.0f;
        if (this.canDestroy(block)) {
            float DestroySpeed = this.getDestroySpeed(block);
            return DestroySpeed / block.getDestroySpeed() / 30.0f;
        } else {
            return this.getDestroySpeed(block) / block.getDestroySpeed() / 100.0f;
        }
    }

    public final float _getItemDestroySpeed(final Block block) {
        return this.getSelectedItem().getDestroySpeed(block);
    }

    @NotImplemented
    public final float getDestroySpeed(final Block block) {
        float speed = this._getItemDestroySpeed(block);
//        int amp = 0;
//        if (this.hasEffect(MobEffect.DIG_SPEED)) {
//            amp = this.getEffect(MobEffect.DIG_SPEED).getAmplifier();
//        }
//        if (this.hasEffect(MobEffect.CONDUIT_POWER)) {
//            amp = Math.max(amp, this.getEffect(MobEffect.CONDUIT_POWER).getAmplifier() + 1);
//        }
//        if (amp > 0)
//            speed = (((float)(amp + 1) * 0.2f) + 1.0f) * speed;
//        if (this.hasEffect(MobEffect.DIG_SLOWDOWN)) {
//            int amplifier = this.getEffect(MobEffect.DIG_SLOWDOWN).getAmplifier();
//            speed = (float) (speed * Math.pow(0.30000001, amplifier + 1));
//        }
//        if (this.isRiding() || (!this.mOnGround && !this.mAbilities.isFlying())) {
//            speed = speed / 5.0f;
//        }
//        if (super.isUnderLiquid(MaterialType.Water_0)){
//            ItemStack armor = EnchantUtils.getRandomItemWith(Enchant.Type.WaterAffinity, this, Armor_0);
//            if (armor == null || !armor.toBoolean())
//                return speed / 5.0;
//        }
        return speed;
    }

    public final int getRespawnDimensionId() {
        return this.mRespawnDimensionId;
    }

    @NotImplemented
    public String getInteractText() {
        return "";
//        return I18n.get(this.mEntityData.getString(ActorDataIDs.INTERACT_TEXT.ordinal()));
    }

    public final void setBedRespawnPosition(final BlockPos bedPosition) {
        Vec3 spawnPosition = super.getStateVectorComponent().getPos();
        spawnPosition.y = this.getAABBShapeComponent().getAABB().min.y;
        BlockPos inRespawnPosition = new BlockPos(spawnPosition);
        this.setRespawnPosition(inRespawnPosition, false);
        if (!this.mBedPosition.equals(bedPosition)) {
            this.mBedPosition = bedPosition;
            this.mEntityData.set(ActorDataIDs.BED_POSITION.ordinal(), bedPosition);
            this.displayLocalizableMessage(ColorFormat.GRAY + "%tile.bed.respawnSet", new ArrayList<>(0));
        }
    }

    public final void setRespawnPosition(final BlockPos inRespawnPosition, boolean forced) {
        this.mRespawnPosition = inRespawnPosition;
        this.mIsForcedRespawnPos = forced;
        if (this.mIsInitialSpawnDone && this.isAlive() && this.mDimensionState != DimensionState.WaitingRespawn && !this.isLoading()) {
            this.moveSpawnView(new Vec3(this.mRespawnPosition));
        }
    }

    public void setCarriedItem(final ItemStack item) {
//        ItemInstance previouslyCarriedItem = new ItemInstance(this.getCarriedItem());
//        ItemInstance carriedItem = new ItemInstance(item);
//        this.getLevel().getActorEventCoordinator().sendActorCarriedItemChanged(this, previouslyCarriedItem, carriedItem, Mainhand);
        this.getSupplies().setSelectedItem(item);
    }

    public void setSelectedItem(final ItemStack item) {
        this.mInventoryProxy.setSelectedItem(item);
    }

    //1.14.60.5 setTeleportDestination
    //1.16.100.56 setRespawnReady
    public final void setTeleportDestination(final Vec3 newTeleportPos) {
        this.mTeleportDestPos = newTeleportPos;
        this.mRespawnReady = true;
    }

    public void setPlayerGameType(GameType gameType) {
        GameType oldGameType;
        Vec3 debugPt_0, debugPt;
        oldGameType = this.mPlayerGameType;
        GameType gameType1;
        if (gameType == GameType.Default) {
            gameType1 = this.getLevel().getDefaultGameType();
        } else {
            gameType1 = gameType;
        }
        if (oldGameType != gameType1 || this.mGameMode == null) {
            switch (gameType1) {
                case Survival:
                case SurvivalViewer:
                    this.mGameMode = new SurvivalMode(this);
                    break;
                default:
                    this.mGameMode = new GameMode(this);
                    break;
            }
        }
        this.mPlayerGameType = gameType;
        if (this.getPlayerGameType() == GameType.CreativeViewer || this.getPlayerGameType() == GameType.SurvivalViewer) {
            if (oldGameType != GameType.CreativeViewer && oldGameType != GameType.SurvivalViewer) {
                this.mSurvivalViewerPosition = this.getInterpolatedPosition(1.0f);
            }
            this.mAbilities.setAbility(AbilitiesIndex.Flying, true);
            this.mAbilities.setAbility(AbilitiesIndex.MayFly, true);
            this.mAbilities.setAbility(AbilitiesIndex.Instabuild, true);
            this.mAbilities.setAbility(AbilitiesIndex.Invulnerable, true);
            this.mAbilities.setAbility(AbilitiesIndex.NoClip, true);
            this.setInvisible(true);
        } else if (this.isCreative()) {
            if (oldGameType == GameType.CreativeViewer || oldGameType == GameType.SurvivalViewer) {
                this.setTeleportDestination(this.mSurvivalViewerPosition);
                this.teleportTo(this.mSurvivalViewerPosition, true, 0, ActorType.Undefined_2.getValue());
                debugPt = this.getInterpolatedPosition(1.0f);
                this.mAbilities.setAbility(AbilitiesIndex.Flying, false);
                this.setInvisible(false);
            }
            this.mAbilities.setAbility(AbilitiesIndex.MayFly, true);
            this.mAbilities.setAbility(AbilitiesIndex.Instabuild, true);
            this.mAbilities.setAbility(AbilitiesIndex.Invulnerable, true);
            this.mAbilities.setAbility(AbilitiesIndex.NoClip, false);
        } else if (this.isSurvival()) {
            if (oldGameType == GameType.CreativeViewer || oldGameType == GameType.SurvivalViewer) {
                this.setTeleportDestination(this.mSurvivalViewerPosition);
                this.teleportTo(this.mSurvivalViewerPosition, true, 0, ActorType.Undefined_2.getValue());
                debugPt_0 = this.getInterpolatedPosition(1.0f);
                this.setInvisible(false);
            }
            this.mAbilities.setAbility(AbilitiesIndex.Flying, false);
            this.mAbilities.setAbility(AbilitiesIndex.MayFly, false);
            this.mAbilities.setAbility(AbilitiesIndex.Instabuild, false);
            this.mAbilities.setAbility(AbilitiesIndex.Invulnerable, false);
            this.mAbilities.setAbility(AbilitiesIndex.NoClip, false);
        }
    }

    @Override
    public void setSleeping(boolean val) {
        this.mPlayerIsSleeping = val;
        super.setSleeping(val);
    }

    public void startCooldown(final Item item) {
        if (item != null && item.getCooldownType() != CooldownType.TypeNone) {
            this.mCooldowns.add(item.getCooldownType().getValue(), item.getCooldownTime());
        }
    }

    public boolean forceAllowEating() {
        if (this.isCreative()) {
            return true;
        }
        return this.getLevel().getDifficulty() == Difficulty.Peaceful;
    }

    @NotImplemented
    public void inventoryChanged(Container container, int slot, final ItemStack oldItem, final ItemStack newItem) {
        InventoryAction action = new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), slot, oldItem, newItem);
        this.mTransactionManager.addAction(action);
//        if (slot == this.getSelectedItemSlot()) {
//            ItemInstance previouslyCarriedItem = new ItemInstance(oldItem);
//            ItemInstance carriedItem = new ItemInstance(newItem);
//            this.getLevel().getActorEventCoordinator().sendActorCarriedItemChanged(this, previouslyCarriedItem, carriedItem, HandSlot.Mainhand);
//        }
    }

    @NotImplemented
    private void registerAttributes() {
        this.mAttributes.registerAttribute(SharedAttributes.ATTACK_DAMAGE).setRange(2.0f, 2.0f, 2.0f);
        this.mAttributes.registerAttribute(SharedAttributes.HEALTH).setRange(0.0f, 20.0f, 20.0f);
//        AttributeInstance hunger = this.mAttributes.registerAttribute(Player.HUNGER);
//        hunger.setRange(0.0f, FoodConstants.MAX_FOOD, FoodConstants.MAX_FOOD);
//        AttributeInstance exhaustion = this.mAttributes.registerAttribute(Player.EXHAUSTION);
//        exhaustion.setDefaultValue(4.0 + 1.0, AttributeOperands.OPERAND_MAX);
//        this.mAttributes.registerAttribute(Player.SATURATION).setRange(0.0f, 5.0f, FoodConstants.MAX_FOOD);
//        this.mAttributes.registerAttribute(Player.LEVEL).setDefaultValue(24791.0f, AttributeOperands.OPERAND_MAX);
//        this.mAttributes.registerAttribute(Player.EXPERIENCE).setDefaultValue(1.0f, AttributeOperands.OPERAND_MAX);
//        hunger.setDelegate(new HungerAttributeDelegate(hunger, this));
//        exhaustion.setDelegate(new ExhaustionAttributeDelegate(exhaustion));
//        exhaustion.registerListener(hunger);
    }

    public final void teleportTo(final Vec3 pos, boolean shouldStopRiding, int cause, int sourceEntityType) {
        float d = this.getStateVectorComponent().getPos().subtract(pos).length();
        super.teleportTo(pos, shouldStopRiding, 0, ActorType.Undefined_2.getValue());
        this.mPositionMode = PositionMode.Teleport_1;
    }

    @Override
    public void sendMotionPacketIfNeeded() {
    }

    public abstract void sendInventoryTransaction(InventoryTransaction transaction);

    public abstract void sendComplexInventoryTransaction(ComplexInventoryTransaction transaction);

    public void sendNetworkPacket(BedrockPacket packet) {
    }

    public void sendInventory(boolean shouldSelectSlot) {
    }


    public void displayChatMessage(final String author, final String message) {
    }

    public void displayClientMessage(final String message) {
    }

    public void displayLocalizableMessage(final String message, final ArrayList<String> params) {
    }

//    public void displayTextObjectMessage(final TextObjectRoot textObject, final String xuid, final String platformId) {
//    }

    public void displayWhisperMessage(final String author, final String message, final String xuid, final String platformId) {
    }

    public void feed(int itemId) {
        if (this.getLevel().isClientSide()) {
            EntityEventPacket p = new EntityEventPacket();
            p.setRuntimeEntityId(this.getRuntimeID());
            p.setType(EntityEventType.EATING_ITEM); //FEED
            p.setData(itemId);
            this.getLevel().getPacketSender().send(p);
        }
    }

    @NotImplemented
    public void moveView() {
    }

    @NotImplemented
    @Override
    public boolean drop(final ItemStack item, boolean randomly) {
//        if (!this.isRegionValid())
//            return false;
        if (this.isUsingItem())
            this.stopUsingItem();
//        if (!this.canUseAbility(AbilitiesIndex.DoorsAndSwitches))
//            return false;
        if (!item.isValid())
            return false;
        if (item.getItem() == null && item.getLegacyBlock() == null) {
            return false;
        }
        super.swing();
        InventoryAction action = new InventoryAction(InventorySource.fromWorldInteraction(InventorySource.InventorySourceFlags.NoFlag), 0, ItemStack.EMPTY_ITEM, item);
        this.getTransactionManager().addAction(action);
        //this.getLevel().getActorEventCoordinator().sendActorDroppedItem(this, new ItemInstance(item));
        return true;
    }

    @NotImplemented
    public final void tickArmor() {
//        if (this.getItemSlot(EquipmentSlot.Head_2).getItem() == VanillaItems.mTurtleHelmet && !this.mHeadInWater) {
//            MobEffectInstance effect = new MobEffectInstance(MobEffect.getId(MobEffect.WATER_BREATHING), 200, 0, 0, 0, 0);
//            this.addEffect(effect);
//        }
    }

    @NotImplemented
    @Override
    public void normalTick() {
        this.setStatusFlag(ActorFlags.HAS_COLLISION, !this.mAbilities.getBool(AbilitiesIndex.NoClip));
        this.getGameMode().tick();
//        Vec3 exhaustionBasePos = new Vec3(this.getPos());
        super.normalTick();
//        this._applyExhaustion(this.getPos().subtract(exhaustionBasePos));
//        this.moveCape();
        if (this.mItemInUse != null && !this.mItemInUse.isNull()) {
            ItemStack item = this.getSupplies().getSelectedItem();
            if (item.toBoolean() && item.equals(this.mItemInUse)
                    && (this.mItemInUseSlot.mSlot == this.mInventoryProxy.getSelectedSlot().mSlot)
                    && (this.mItemInUseSlot.mContainerId == this.mInventoryProxy.getSelectedSlot().mContainerId)) {
                if (--this.mItemInUseDuration <= 25 && this.mItemInUseDuration % 4 == 0 && item.hasFeedingAnimation()) {
                    this.feed(item.getIdAux());
                }
                if (this.mItemInUseDuration == 0)
                    this.completeUsingItem();
            } else {
                this.stopUsingItem();
            }
        }
//        this._updateInteraction();
//        if (this.getStateVectorComponent().getPosDelta().y < 0.0f && this.isBouncing() ){
//            this.mBounceHeight = (int)(this.getStateVectorComponent().getPos().y - this.mBounceStartPos.y);
//            if (this.mBounceBlock != null && this.mBounceHeight >= 1 ) {
//                this.getEventing().fireEventPlayerBounced(this, this.mBounceBlock, this.mBounceHeight);
//            }
//            this.mBounceBlock = BedrockBlocks.mAir;
//        }
        boolean blockedUsingShield = this.getStatusFlag(ActorFlags.BLOCKED_USING_SHIELD);
        boolean blockedUsingDamagedShield = this.getStatusFlag(ActorFlags.BLOCKED_USING_DAMAGED_SHIELD);
//        if (this.getStatusFlag(ActorFlags.TRANSITION_BLOCKING) || !this.getStatusFlag(ActorFlags.BLOCKING)) {
//            this._setBlockedUsingShieldTimeStamp(0L);
//            this._setBlockedUsingDamagedShieldTimeStamp(0L);
//        } else {
//            if (blockedUsingShield && !this.mPrevBlockedUsingShield) {
//                this._setBlockedUsingShieldTimeStamp(this.getLevel().getCurrentTick());
//            }
//            if (blockedUsingDamagedShield && !this.mPrevBlockedUsingDamagedShield) {
//                this._setBlockedUsingDamagedShieldTimeStamp(this.getLevel().getCurrentTick());
//            }
//        }
        this.mPrevBlockedUsingShield = blockedUsingShield;
        this.mPrevBlockedUsingDamagedShield = blockedUsingDamagedShield;

//        if (this.mElytraLoop == -1L)
//            this._registerElytraLoopSound();
        this.mPositionMode = PositionMode.Normal_4;
        if (this.mUsedPotion)
            this.mUsedPotion = false;
        if (this.mOnScreenAnimationTicks > 0) {
            if (--this.mOnScreenAnimationTicks == 0) {
                this.mOnScreenAnimationTextures.remove(0);
//                if (this.mOnScreenAnimationTextures.size() > 0)
//                    this.resetOnScreenAnimationTicks();
            }
        }
    }

    public final void updateInventoryTransactions() {
        if (this.mTransactionManager.getCurrentTransaction() != null){
            boolean isClientSide = this.getLevel().isClientSide();
            if (isClientSide && this.isInCreativeMode()) {
                this.mTransactionManager.getCurrentTransaction()._logTransaction(isClientSide);
                this.mTransactionManager._logExpectedActions();
                this.mTransactionManager.forceBalanceTransaction();
            }
            if (this.mTransactionManager.getCurrentTransaction() != null) {
                this.mTransactionManager.getCurrentTransaction()._logTransaction(isClientSide);
                this.mTransactionManager._logExpectedActions();
                if (isClientSide) {
                    ComplexInventoryTransaction transaction = ComplexInventoryTransaction.fromType(ComplexInventoryTransaction.Type.NormalTransaction, this.mTransactionManager.getCurrentTransaction());
                    this.mPacketSender.send(DataConverter.inventoryTransactionPacket(transaction));
                }
                this.mTransactionManager.reset();
            }
        }
        this.mTransactionManager.resetExpectedActions();
    }

    @NotImplemented
    public void causeFoodExhaustion(float exhaustionAmount) {
    }

    @NotImplemented
    public void respawn() {
        this.reset();
//        if (this.mIsInitialSpawnDone && super.getHealth() <= 0) {
//            BreathableComponent breathable = super.tryGetComponent<BreathableComponent>();
//            if (breathable != null) {
//                breathable.setAirSupply(breathable.getMaxAirSupply());
//            }
//            InsomniaComponent insomniaComponent = super.tryGetComponent<InsomniaComponent>();
//            if (insomniaComponent != null)
//                insomniaComponent.restartTimer();
//        }
        this.resetPos(super.getHealth() <= 0);
        if (super.getHealth() > 0 ) {
            this.mDeathTime = 0;
            this.mDead = false;
        }
        this.setPos(this.mTeleportDestPos);
        super._setPosPrev(this.mTeleportDestPos);
        if (this.mIsInitialSpawnDone) {
            if (super.getLevel().isClientSide()) {
                long runtimeId = this.getRuntimeID();
                PlayerActionPacket packet = new PlayerActionPacket();
                packet.setAction(PlayerActionType.RESPAWN);
                packet.setBlockPosition(Vector3i.ZERO);
                packet.setFace(-1);
                packet.setRuntimeEntityId(runtimeId);
                this.mPacketSender.send(packet);
            }
        }
//        if (!this.getLevel().isClientSide()) {
//            SetHealthPacket packet_0 = new SetHealthPacket(super.getHealth());
//            mPacketSender.sendToClient(this.mOwner, packet_0, this.getClientSubId());
//        }
        this.mRespawnReady = false;
        this.mRespawnBlocked = false;
        this.mSpawnedXP = false;
    }

    @NotImplemented
    public void resetPos(boolean clearMore) {
        super.getStateVectorComponentNonConst().getPosDelta().set(Vec3.ZERO);
        this.mRot.x = 0.0f;
//        this.mInterpolation.reset();
        if (!this.isSleeping()) {
            super._setHeightOffset(1.62001f);
            this.setSize(0.60000002f, 1.8f);
//            boolean hasEffect = this.hasEffect(MobEffect.INVISIBILITY);
//            super.setInvisible(hasEffect);
        }
        this.mImmobile = false;
        if (clearMore) {
            this.removeAllEffects();
            this.getMutableAttribute(SharedAttributes.HEALTH).resetToMaxValue();
            this.getMutableAttribute(Player.HUNGER).resetToDefaultValue();
            this.getMutableAttribute(Player.EXHAUSTION).resetToDefaultValue();
            this.getMutableAttribute(Player.SATURATION).resetToDefaultValue();

            super.setStatusFlag(ActorFlags.GLIDING, false);
            super.setStatusFlag(ActorFlags.SWIMMING, false);
//            if (!super.getLevel().getGameRules().getBool(11)){
//                this.resetPlayerLevel();
//                this.mScore = 0;
//            }
            this.mOnFire = 0;
            this.mDeathTime = 0;
            this.setSleeping(false);
            this.mFallDistance = 0.0f;
            super.setInvisible(false);
        }
    }

    @Override
    public void _onSizeUpdated() {
        this.mHeadOffset = new Vec3(0.0f, -0.1f, 0.0f);
        this.mEyeOffset = new Vec3(0.0f, -0.1f, 0.0f);
        this.mBreathingOffset = new Vec3(0.0f, 0.0f, 0.0f);
        this.mMouthOffset = new Vec3(0.0f, -0.2f, 0.2f);
    }

    @NotImplemented
    public void moveSpawnView(final Vec3 spawnPosition) {
    }

    @NotImplemented
    public void destroyRegion() {
    }

    @NotImplemented
    public void suspendRegion() {
    }

    @NotImplemented
    public void prepareRegion(ChunkSource chunkSource) {
    }

    @NotImplemented
    public void _fireWillChangeDimension() {
    }

    @NotImplemented
    public void _fireDimensionChanged() {
    }

    public final boolean interact(Actor entity, final Vec3 location) {
        ActorInteraction interaction = new ActorInteraction(false);
        if (entity.getInteraction(this, interaction, location)) {
            interaction.interact();
            if (entity.hasCategory(ActorCategory.Mob_0))
                entity.setPersistent();
            return true;
        } else {
            return false;
        }
    }
//----------------------------------------------------------------------------------------------------------------------

    public String getName() {
        return mName;
    }

    public UUID getClientUUID() {
        return mClientUUID;
    }

    public void setClientUUID(UUID uuid) {
        this.mClientUUID = uuid;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setMovementMode(AuthoritativeMovementMode movementMode) {
        this.movementMode = movementMode;
    }

    public boolean isInitialSpawnDone() {
        return mIsInitialSpawnDone;
    }

    public void setInitialSpawnDone(boolean initialSpawnDone) {
        this.mIsInitialSpawnDone = initialSpawnDone;
    }

//----------------------------------------------------------------------------------------------------------------------

    public enum DimensionState {
        Ready_0,
        Pending,
        WaitingServerResponse,
        WaitingArea,
        WaitingRespawn,
    }

    public enum PositionMode {
        Normal_4,
        Respawn_0,
        Teleport_1,
        OnlyHeadRot,
    }

    public enum SpawnPositionSource {
        Randomizer,
        Respawn_2,
        Teleport_3,
        Static,
    }

    public enum SpawnPositionState {
        InitializeSpawnPositionRandomizer,
        WaitForClientAck,
        ChangeDimension_0,
        WaitForDimension,
        ChooseSpawnArea,
        CheckLoadedChunk,
        ChooseSpawnPosition,
        SpawnComplete
    }
}
