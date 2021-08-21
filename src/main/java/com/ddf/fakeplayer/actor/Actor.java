package com.ddf.fakeplayer.actor;

import com.ddf.fakeplayer.actor.attribute.Attribute;
import com.ddf.fakeplayer.actor.attribute.AttributeInstance;
import com.ddf.fakeplayer.actor.attribute.BaseAttributeMap;
import com.ddf.fakeplayer.actor.attribute.SharedAttributes;
import com.ddf.fakeplayer.actor.component.AABBShapeComponent;
import com.ddf.fakeplayer.actor.component.StateVectorComponent;
import com.ddf.fakeplayer.actor.definition.ActorDefinitionIdentifier;
import com.ddf.fakeplayer.actor.mob.effect.MobEffect;
import com.ddf.fakeplayer.actor.mob.effect.MobEffectInstance;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.actor.player.permission.CommandPermissionLevel;
import com.ddf.fakeplayer.block.BedrockBlocks;
import com.ddf.fakeplayer.block.Block;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.block.BlockSource;
import com.ddf.fakeplayer.container.ContainerType;
import com.ddf.fakeplayer.container.SimpleContainer;
import com.ddf.fakeplayer.container.slot.ArmorSlot;
import com.ddf.fakeplayer.container.slot.HandSlot;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.level.chunk.ChunkPos;
import com.ddf.fakeplayer.level.dimension.Dimension;
import com.ddf.fakeplayer.level.dimension.VanillaDimensions;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.*;
import com.ddf.fakeplayer.util.tuple.Tuple2;
import com.nukkitx.protocol.bedrock.packet.InteractPacket;

import java.util.*;

@SuppressWarnings("all")
public class Actor {
    //OwnerPtr<EntityId> mEntity = null;
    //private Actor.InitializationMethod mInitMethod = InitializationMethod.INVALID_0;
    private String mCustomInitEventName = "";
    //private VariantParameterList mInitParams = new VariantParameterList();
    private boolean mForceInitMethodToSpawnOnReload = false;
    private boolean mRequiresReload = false;
    private /*DimensionType*/int mDimensionId = VanillaDimensions.Undefined;
    private boolean mAdded = false;
    //private ActorDefinitionGroup mDefinitions = definitions;
    //private ActorDefinitionDescriptor mCurrentDescription = new ActorDefinitionDescriptor();
    private /*ActorUniqueID*/long mUniqueID = ActorUniqueID.INVALID_ID;
    //private RopeSystem mLeashRopeSystem = new RopeSystem();
    public Vec2 mRot = new Vec2(Vec2.ZERO);
    public Vec2 mRotPrev = new Vec2(Vec2.ZERO);
    private float mSwimAmount = 0.0f;
    private float mSwimPrev = 0.0f;
    private ChunkPos mChunkPos = new ChunkPos();
    private Vec3 mRenderPos = new Vec3(Vec3.ZERO);
    private Vec2 mRenderRot = new Vec2(Vec2.ZERO);
    private int mAmbientSoundTime = 0;
    private int mLastHurtByPlayerTime = 0;
    protected /*ActorCategory*/int mCategories = ActorCategory.None_21.getValue();
    protected SynchedActorData mEntityData = new SynchedActorData();
    //private SpatialActorNetworkData mNetworkData = new SpatialActorNetworkData();
    private Vec3 mSentDelta = new Vec3(Vec3.ZERO);
    private float mScale = 1.0f;
    private float mScalePrev = 0.0f;
    private /*uint64_t HashType64*/long mNameTagHash;
    public boolean mOnGround = true;
    protected boolean mWasOnGround = true;
    private boolean mHorizontalCollision = false;
    private boolean mVerticalCollision = false;
    private boolean mCollision = false;
    private Block mInsideBlock = BedrockBlocks.mAir;
    private BlockPos mInsideBlockPos = new BlockPos();
    protected float mFallDistance = 0.0f;
    private boolean mIgnoreLighting = false;
    private boolean mFilterLighting = false;
    //private Color mTintColor = new Color();
    //private Color mTintColor2 = new Color();
    private float mStepSoundVolume = 0.25f;
    private float mStepSoundPitch = 1.0f;
    private AABB mLastHitBB;
    private ArrayList<AABB> mSubBBs = new ArrayList<>();
    private float mTerrainSurfaceOffset = 0.0f;
    public float mHeightOffset = 0.0f;
    private float mExplosionOffset = 0.0f;
    private float mShadowOffset = 0.0f;
    protected float mMaxAutoStep = 0.0f;
    private float mPushthrough = 0.0f;
    private float mWalkDistPrev = 0.0f;
    private float mWalkDist = 0.0f;
    private float mMoveDist = 0.0f;
    private int mNextStep = 1;
    private float mBlockMovementSlowdownMultiplier = 0.0f;
    protected boolean mImmobile = false;
    private boolean mWasInWater = false;
    private boolean mHasEnteredWater = false;
    protected boolean mHeadInWater = false;
    private boolean mIsWet = false;
    private Vec2 mSlideOffset = new Vec2(Vec2.ZERO);
    protected Vec3 mHeadOffset = new Vec3(Vec3.ZERO);
    protected Vec3 mEyeOffset = new Vec3(Vec3.ZERO);
    protected Vec3 mBreathingOffset = new Vec3(Vec3.ZERO);
    protected Vec3 mMouthOffset = new Vec3(Vec3.ZERO);
    private Vec3 mDropOffset = new Vec3(0.0f, -0.2f, 0.15000001f);
    private boolean mFirstTick = true;
    private int mTickCount = 0;
    private int mInvulnerableTime = 0;
    private int mLastHealth = 0;
    private boolean mFallDamageImmune = false;
    private boolean mHurtMarked = false;
    private boolean mWasHurtLastFrame = false;
    private boolean mInvulnerable = false;
    private boolean mAlwaysFireImmune = false;
    protected int mOnFire = 0;
    private int mFlameTexFrameIndex = 0;
    private float mFlameFrameIncrementTime = 0.0f;
    private boolean mOnHotBlock = false;
    private int mClientSideFireTransitionStartTick = -1;
    private boolean mClientSideFireTransitionLatch = false;
    private int mPortalCooldown = 0;
    private BlockPos mPortalBlockPos = new BlockPos(BlockPos.MAX);
    //private PortalAxis mPortalEntranceAxis = PortalAxis.Unknown_33;
    private int mInsidePortalTime = 0;
    protected ArrayList</*ActorUniqueID*/Long> mRiderIDs = new ArrayList<>();
    private ArrayList</*ActorUniqueID*/Long> mRiderIDsToRemove = new ArrayList<>();
    private /*ActorUniqueID*/long mRidingID = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mRidingPrevID = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mPushedByID = ActorUniqueID.INVALID_ID;
    private boolean mInheritRotationWhenRiding = false;
    private boolean mRidersChanged = false;
    private boolean mBlocksBuilding = false;
    private boolean mUsesOneWayCollision = false;
    private boolean mForcedLoading = false;
    private boolean mPrevPosRotSetThisTick = false;
    protected boolean mTeleportedThisTick = false;
    private boolean mForceSendMotionPacket = false;
    private float mSoundVolume = 1.0f;
    private int mShakeTime = 0;
    private float mWalkAnimSpeedMultiplier = 1.0f;
    private float mWalkAnimSpeedO = 0.0f;
    private float mWalkAnimSpeed = 0.0f;
    private float mWalkAnimPos = 0.0f;
    private /*ActorUniqueID*/long mLegacyUniqueID = ActorUniqueID.INVALID_ID;
    private boolean mHighlightedThisFrame = false;
    private boolean mInitialized = false;
    private BlockSource mRegion = null;
    private Dimension mDimension = null;
    private Level mLevel;
    private String mActorRendererId = "";
    private String mActorRendererIdThatAnimationComponentWasInitializedWith = "";
    private boolean mChanged = false;
    private boolean mRemoved = false;
    private boolean mGlobal = false;
    private boolean mAutonomous = false;
    private ActorType mActorType = ActorType.Undefined_2;
    private ActorDefinitionIdentifier mActorIdentifier = new ActorDefinitionIdentifier(ActorType.Undefined_2);
    protected BaseAttributeMap mAttributes = new BaseAttributeMap();
    //private EconomyTradeableComponent mEconomyTradeableComponent = new EconomyTradeableComponent();
    //private AnimationComponent mAnimationComponent = new AnimationComponent();
    private final AABBShapeComponent mAABBComponent = new AABBShapeComponent();
    private final StateVectorComponent mStateVectorComponent = new StateVectorComponent();;
    private /*ActorUniqueID*/long mTargetId = ActorUniqueID.INVALID_ID;
    private boolean mLootDropped = false;
    private float mRestrictRadius = -1.0f;
    private BlockPos mRestrictCenter = new BlockPos(BlockPos.ZERO);
    private /*ActorUniqueID*/long mInLovePartner = ActorUniqueID.INVALID_ID;
    private ArrayList<MobEffectInstance> mMobEffects = new ArrayList<>();
    private boolean mEffectsDirty = false;
    private boolean mPersistingTrade = false;
    private CompoundTag mPersistingTradeOffers = new CompoundTag();
    private int mPersistingTradeRiches = 0;
    private /*ActorRuntimeID*/long mRuntimeID = 0L;
    //private Color mHurtColor = new Color(1.0f, 0.0f, 0.0f, 0.60000002f);
    private boolean mEnforceRiderRotationLimit = false;
    //private ActorDefinitionDiffList mDefinitionList;
    private boolean mHasLimitedLife = false;
    private int mLimitedLifeTicks = 0;
    private int mForceVisibleTimerTicks = 0;
    private String mFilteredNameTag = "";
    private boolean mIsStuckItem = false;
    private float mRidingExitDistance = 1.6f;
    private boolean mIsSafeToSleepNear = true;
    //private ActorTerrainInterlockData mTerrainInterlockData = new ActorTerrainInterlockData();
    protected SimpleContainer mArmor = new SimpleContainer("", false, ArmorSlot._count_1.ordinal(), ContainerType.INVENTORY);
    private float[] mArmorDropChance = new float[4];
    protected SimpleContainer mHand = new SimpleContainer("", false, HandSlot._count_0.ordinal(), ContainerType.INVENTORY);
    private float[] mHandDropChance = new float[2];
    protected boolean mIsKnockedBackOnDeath = false;
    private ArrayList<AABB> mOnewayPhysicsBlocks = new ArrayList<>();
    private boolean mStuckInCollider = false;
    private float mLastPenetration = 0.0f;
    private boolean mCollidableMobNear = false;
    private boolean mCollidableMob = false;
    private boolean mChainedDamageEffects = false;
    private int mDamageNearbyMobsTick = 0;
    private boolean mWasInBubbleColumn = false;
    private boolean mWasInWallLastTick = false;
    private int mTicksInWall = 0;
    private boolean mIsExperimental = false;
    //private ActionQueue mActionQueue = new ActionQueue();
    //private MolangVariableMap mMolangVariables = new MolangVariableMap();
    private CompoundTag mCachedComponentData = new CompoundTag();
    private /*ActorUniqueID*/long mFishingHookID = ActorUniqueID.INVALID_ID;

    private String identifier;
    private int entityType;

    @NotImplemented
    private Actor(/*ActorDefinitionGroup*/Object definitions, Level level) {
        this.mLevel = level;
        //if (this.mDefinitionList != null)
        //    this.mDefinitionList.mDefinitions = new ActorDefinitionGroup();
        //else
        //    this.mDefinitionList = new ActorDefinitionDiffList();
        EntityUtil._defineEntityData(this.mEntityData);
    }

    @NotImplemented
    public Actor(/*ActorDefinitionGroup*/Object definitions, final ActorDefinitionIdentifier definitionName) {
        this(definitions, (Level) null);
//        this.setBaseDefinition(definitionName, 1, 1);
        this.mActorRendererId = definitionName.getCanonicalHash();
    }

    @NotImplemented
    public Actor(Level level) {
        this(null, level);
//        this(level.getEntityDefinitions(), level);
//        String entityName = "";
//        String entityNamespace = "";
//        Tuple2<String, String> result = EntityUtil.EntityTypeToStringAndNamespace(ActorType.Player_0, entityName, entityNamespace);
//        entityName = result.getT1();
//        entityNamespace = result.getT2();
//        String nameOut = entityName;
//        ActorDefinitionIdentifier actorID = new ActorDefinitionIdentifier(entityNamespace, entityName, nameOut);
//        this.setBaseDefinition(actorID, 1, 1);
        this.getStateVectorComponentNonConst().getPosDelta().set(Vec3.ZERO);
    }

    public final void _refreshAABB() {
        Vec3 pos = this.getStateVectorComponent().getPos();
        float w = this.getAABBShapeComponent().getAABBDim().x / 2.0f;
        float h = this.getAABBShapeComponent().getAABBDim().y;
        float v5 = (pos.y - this.mHeightOffset) + this.mSlideOffset.y;
        this._getAABBShapeComponentNonConst().setAABB(new AABB(pos.x - w, v5, pos.z - w, pos.x + w, v5 + h, pos.z + w));
    }

    private static Tuple2<Float, Float> _rotationWrapWithInterpolation(float rot, float oRot) {
        float newRot = MathUtil.wrapDegrees(rot);
        oRot = MathUtil.wrapDegrees(oRot + (newRot - rot));
        rot = newRot;
        return new Tuple2<>(rot, oRot);
    }

    @NotImplemented
    private void _exitRide(final Actor riding, float exitDist) {
    }

    @NotImplemented
    public void _removeRider(final /*ActorUniqueID*/long id, boolean entityIsBeingDestroyed, boolean switchingRides) {
        if (this.isRider(id)) {
            this.mRiderIDs.remove(id);
            this.mRidersChanged = true;
            if (!switchingRides) {
                ActorLink link = new ActorLink();
                link.type = ActorLinkType.None_13;
                link.A = this.getUniqueID();
                link.B = id;
                link.mImmediate = entityIsBeingDestroyed;
                this._sendLinkPacket(link);
            }
//            Actor rider = this.getLevel().fetchEntity(id, false);
//            if (rider != null) {
//                BoostableComponent boostable = this.tryGetComponent<BoostableComponent>();
//                if (boostable != null) {
//                    if (rider.hasCategory(ActorCategory.Player_1))
//                        boostable.removeRider((Player) rider);
//                }
//            }
        }
    }

    private StateVectorComponent _getStateVectorComponentNonConst() {
        return this.mStateVectorComponent;
    }

    public final Vec2 getRotation() {
        Vec2 rot = new Vec2(this.mRot.x, this.mRot.y);
        return rot;
    }

    public float getYHeadRot() {
        return 0.0f;
    }

    public final void setJumpDuration(int duration) {
        this.mEntityData.set(ActorDataIDs.JUMP_DURATION.ordinal(), (byte) duration);
    }

    public void startSpinAttack() {
        this.mDamageNearbyMobsTick = 0;
    }


    public final Level _getLevelPtr() {
        return this.mLevel;
    }

    @NotImplemented
    public final boolean getStatusFlag(ActorFlags flag) {
        ActorDataIDs id = ActorDataIDs.FLAGS;
        if (flag.ordinal() >= 64)
            id = ActorDataIDs.FLAGS2;
        int flagValue = flag.getValue() % 64;
        return this.mEntityData.getFlag(id.ordinal(), flagValue);
    }

    public final int getHealth() {
        return (int) this.getAttribute(SharedAttributes.HEALTH).getCurrentValue();
    }

    @Deprecated
    public AttributeInstance getAttribute(final String attribute) {
        return this.mAttributes.getInstance(attribute);
    }

    public AttributeInstance getAttribute(final Attribute attribute) {
        return this.mAttributes.getInstance(attribute);
    }

    public final Vec3 getInterpolatedPosition(float a) {
        Vec3 pos = this.getStateVectorComponent().getPos();
        Vec3 posPrev = this.getStateVectorComponent().getPosPrev();
        return new Vec3(
            posPrev.x + ((pos.x - posPrev.x) * a),
            posPrev.y + ((pos.y - posPrev.y) * a),
            posPrev.z + ((pos.z - posPrev.z) * a));
    }

    @Deprecated
    public AttributeInstance getMutableAttribute(final String attribute) {
        return this.mAttributes.getMutableInstance(attribute);
    }

    public AttributeInstance getMutableAttribute(final Attribute attribute) {
        return this.mAttributes.getMutableInstance(attribute);
    }

    public int getDimensionId() {
        return this.mDimensionId;
    }

    @NotImplemented
    public CommandPermissionLevel getCommandPermissionLevel() {
        return CommandPermissionLevel.Any;
//        NpcComponent npcComponent = this.tryGetComponent<NpcComponent>();
//        if (npcComponent != null)
//            return npcComponent.getCommandPermissionLevel();
//        else
//            return CommandPermissionLevel.Any;
    }

    @NotImplemented
    public final MobEffectInstance getEffect(final MobEffect effect) {
        return null;
//        if (this.mMobEffects.size() <= effect.getId())
//            return null;
//        int id = effect.getId();
//        if (this.mMobEffects.get(id).equals(MobEffectInstance.NO_EFFECT))
//            return null;
//        return this.mMobEffects.get(id);
    }

    public final BlockSource getRegion() {
        return this.mRegion;
    }

    public Vec3 getPos() {
        return this.getStateVectorComponent().getPos();
    }

    public final Actor getRide() {
        if (this.mRidingID == ActorUniqueID.INVALID_ID)
            return null;
        return this.getLevel().fetchEntity(this.mRidingID, false);
    }

    public final Level getLevel() {
        return this.mLevel;
    }

    public final AABBShapeComponent _getAABBShapeComponentNonConst() {
        return this.mAABBComponent;
    }

    public final AABBShapeComponent getAABBShapeComponent() {
        return this.mAABBComponent;
    }

    public final StateVectorComponent getStateVectorComponentNonConst() {
        return this.mStateVectorComponent;
    }

    public final StateVectorComponent getStateVectorComponent() {
        return this.mStateVectorComponent;
    }

    public final long getRuntimeID() {
        return this.mRuntimeID;
    }

    public final long getUniqueID() {
        if (!ActorUniqueID.toBoolean(this.mUniqueID)) {
            this.mUniqueID = this._getLevelPtr().getNewUniqueID();
        }
        return this.mUniqueID;
    }

    public long getControllingPlayer() {
        return ActorUniqueID.INVALID_ID;
    }

    public ItemStack getArmor(ArmorSlot slot) {
        return this.mArmor.getItem(slot.ordinal());
    }

    public float getArmorColorInSlot(ArmorSlot a2, int a3) {
        return 1.0f;
    }

    public final SimpleContainer getArmorContainer() {
        return this.mArmor;
    }

    public final SimpleContainer getHandContainer() {
        return this.mHand;
    }

    public ItemStack getOffhandSlot() {
        return this.mHand.getItem(HandSlot.Offhand.ordinal());
    }

    public ActorType getEntityTypeId() {
        return this.mActorType;
    }

    public boolean canChangeDimensions() {
        return true;
    }

    @NotImplemented
    public boolean canBeAffected(int id) {
        return false;
//        if (this.isAlive() && this.hasType(ActorType.Mob)) {
//            return !this.hasFamily("undead") || id != MobEffect.REGENERATION.getId() && id != MobEffect.POISON.getId();
//        } else {
//            return false;
//        }
    }

    @NotImplemented
    public boolean canBeAffected(final MobEffectInstance effect) {
        return false;
//        return this.canBeAffected(effect.getId());
    }

    @NotImplemented
    public final boolean hasFamily(final String family) {
        return false;
//        if (this.mCurrentDescription != null && this.mCurrentDescription.mFamilyTypes) {
//            return this.mCurrentDescription.mFamilyTypes.contains(family);
//        } else {
//            return false;
//        }
    }

    public final boolean hasCategory(ActorCategory categories) {
        return (this.mCategories & categories.getValue()) == categories.getValue();
    }

    public final boolean hasEffect(final MobEffect effect) {
        return this.getEffect(effect) != null;
    }

    public final boolean hasLevel() {
        return this.mLevel != null;
    }

    public final boolean hasRider() {
        return this.mRiderIDs.size() != 0;
    }

    public final boolean hasType(ActorType types) {
        return this.getEntityTypeId() == types;
    }

    public boolean isAlive() {
        return !this.mRemoved;
    }

    public boolean isWorldBuilder() {
        return false;
    }

    public boolean isAdventure() {
        return false;
    }

    public boolean isCreative() {
        return false;
    }

    public boolean isRemoved() {
        return this.mRemoved;
    }

    public final boolean isRiding() {
        return this.getRide() != null;
    }

    public boolean isRiding(Actor targetRide) {
        for (Actor ride = this.getRide(); ride != null; ride = ride.getRide()) {
            if ( ride == targetRide ) {
                return true;
            }
        }
        return false;
    }

    public final boolean isRider(final /*ActorUniqueID*/long id) {
        return this.mRiderIDs.contains(id);
    }

    @NotImplemented
    public boolean isInWaterOrRain() {
//        Vec3 pos = this.getPos().add(0, this.getAABBShapeComponent().getAABBDim().y, 0);
//        if (!this.mWasInWater && !this.getDimension().getWeather().isRainingAt(this.getRegion(), new BlockPos(pos))) {
//            return this.mIsWet;
//        }
        return true;
    }

    @NotImplemented
    public final boolean isInWorld() {
        return this.mAdded
                && this.mRegion != null
                && !this.mRemoved;
    }

    public final boolean isControlledByLocalInstance() {
        Player player = this.getLevel().getPlayer(this.getControllingPlayer());
        if (player != null) {
            return player.isLocalPlayer();
        } else {
            return !this.getLevel().isClientSide();
        }
    }

    public final boolean isGlobal() {
        return this.mGlobal;
    }

    public final boolean isRegionValid() {
        return this.mRegion != null;
    }

    public final boolean isSwimming() {
        return this.getStatusFlag(ActorFlags.SWIMMING);
    }

    public boolean isSleeping() {
        return false;
    }

    protected final void _setHeightOffset(final float heightOffset) {
        this.mHeightOffset = heightOffset;
        this._refreshAABB();
    }

    private void _setDimension(Dimension dimension) {
        this.mDimensionId = dimension.getId();
        this.mDimension = dimension;
    }

    @Deprecated
    public void _setDimensionId(int id) {
        this.mDimensionId = id;
    }

    public void setRot(final Vec2 rot) {
        this.mRot = rot;
        Tuple2<Float, Float> result = _rotationWrapWithInterpolation(this.mRot.y, this.mRotPrev.y);
        this.mRot.y = result.getT1();
        this.mRotPrev.y = result.getT2();
        result = _rotationWrapWithInterpolation(this.mRot.x, this.mRotPrev.x);
        this.mRot.x = result.getT1();
        this.mRotPrev.x = result.getT2();
    }

    public void setPos(final Vec3 pos) {
        this._getStateVectorComponentNonConst()._setPos(pos);
        this._refreshAABB();
        if (this.mAdded) {
            if (!this.getLevel().isClientSide())
                this._updateOwnerChunk();
        }
    }

    public void setArmor(ArmorSlot slot, final ItemStack item) {
//        if (this.hasLevel()) {
//            this.getLevel().getActorEventCoordinator().sendActorEquippedArmor(this, new ItemInstance(item), slot);
//        }
        this.mArmor.setItem(slot.ordinal(), item);
    }

    public void setEquippedSlot(ArmorSlot slot, final ItemStack item) {
    }

    public void setEquippedSlot(ArmorSlot slot, int item, int auxValue) {
    }

    @NotImplemented
    public final void setStatusFlag(ActorFlags flag, boolean value) {
    }

    public void setInvisible(boolean value) {
        this.setStatusFlag(ActorFlags.CAN_SHOW_NAME, !value);
        this.setStatusFlag(ActorFlags.INVISIBLE, value);
    }

    public final void setEnforceRiderRotationLimit(boolean value) {
        this.mEnforceRiderRotationLimit = value;
    }

    protected final void _setPosPrev(final Vec3 posPrev) {
        this._getStateVectorComponentNonConst()._setPosPrev(posPrev);
    }

    public final void setPosPrev(Vec3 posPrev) {
        this._getStateVectorComponentNonConst()._setPosPrev(posPrev);
    }

    @NotImplemented
    public final void setRegion(BlockSource source) {
//        if (!this.hasTickingArea()) {
//            this._setDimension(this.mRegion.getDimension());
//        }
    }

    public final void setRuntimeID(long ID) {
        this.mRuntimeID = ID;
    }

    public final void setUniqueID(/*ActorUniqueID*/long ID) {
        this.mUniqueID = ID;
    }

    public void setSize(float w, float h) {
        float scale = this.mEntityData.getFloat(ActorDataIDs.SCALE.ordinal());
        float xscale = w * scale;
        float yscale = h * scale;
        if (xscale < 0.0049999999f)
            xscale = 0.0049999999f;
        if (yscale < 0.0049999999f)
            yscale = 0.0049999999f;
        Vec2 dim = this.getAABBShapeComponent().getAABBDim();
        if (xscale != dim.x || yscale != dim.y) {
            dim.x = xscale;
            dim.y = yscale;
            this._getAABBShapeComponentNonConst().setAABBDim(dim);
            float halfWidth = 0.5f * dim.x;
            Vec3 pos = this.getPos();
            float minX = pos.x - halfWidth;
            float minY = this.getAABBShapeComponent().getAABB().min.y;
            float minZ = pos.z - halfWidth;
            float maxX = pos.x + halfWidth;
            float maxY = this.getAABBShapeComponent().getAABB().min.y + dim.y;
            float maxZ = pos.z + halfWidth;
            AABB aabb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
            this._getAABBShapeComponentNonConst().setAABB(aabb);
            this.mEntityData.set(ActorDataIDs.WIDTH.ordinal(), w);
            this.mEntityData.set(ActorDataIDs.HEIGHT.ordinal(), h);
            this._onSizeUpdated();
            this.mLastPenetration = 0.0f;
        }
    }

    public void _onSizeUpdated() {
    }

    @NotImplemented
    public void onEffectUpdated(final MobEffectInstance effect) {
        this.mEffectsDirty = true;
//        Level level = this._getLevelPtr();
//        if (level != null && level.isClientSide() && this.hasCategory(ActorCategory.Player_1))
//            MinecraftEventing.fireEventMobEffectChanged(this, effect, Updated);
    }

    @NotImplemented
    public void swing() {
    }

    @NotImplemented
    public final void initEntity(/*EntityRegistryOwned*/Object registry) {
//        this.mEntity = registry;
//        this.mEntity.getOrAddComponent<ActorComponent>().initialize(this);
    }

    public void reset() {
        this.mInitialized = false;
//        if (!this.getLevel().isClientSide())
//            this.initializeComponents(this.mInitMethod, this.mInitParams);
        this.mInitialized = true;
    }

    @NotImplemented
    public final void removeAllEffects() {
//        this.removeEffectParticles();
//        for (int i = 0; i < 30; ++i)
//            this.removeEffect(i);
//        this.removeEffectParticles();
    }

    public void lerpMotion(final Vec3 delta) {
        this.getStateVectorComponentNonConst()
                .getPosDelta()
                .set(delta);
    }

    @NotImplemented
    public void move(Vec3 delta) {
    }

    public final void moveTo(final Vec3 pos, final Vec2 rot) {
        this.setRot(rot);
        this.setPos(pos.add(0.0f, this.mHeightOffset, 0.0f));
    }

    public void teleportTo(final Vec3 pos, boolean shouldStopRiding, int cause, int entityType) {
        if (shouldStopRiding && this.isRiding())
            this.stopRiding(true, true, false);
        this.mFallDistance = 0.0f;
        StateVectorComponent svc = this.getStateVectorComponentNonConst();
        svc.getPosDelta().z = 0.0f;
        svc.getPosDelta().y = 0.0f;
        svc.getPosDelta().x = 0.0f;
        this.moveTo(pos, this.mRot);
        Vec3 newPos = this.getStateVectorComponent().getPos();
        this._getStateVectorComponentNonConst()._setPosPrev(newPos);
        this.mTeleportedThisTick = true;
        this.mForceSendMotionPacket = true;
        if (!this.getLevel().isClientSide())
            this.sendMotionPacketIfNeeded();
        //this.getLevel().getActorEventCoordinator().sendActorTeleported(this);
    }

    public void flagRiderToRemove(Actor r) {
        this.mRiderIDsToRemove.add(r.getUniqueID());
    }

    public void stopRiding(boolean exitFromRider, boolean actorIsBeingDestroyed, boolean switchingRides) {
        Actor ride = this.getRide();
        if (ride != null) {
            if (exitFromRider)
                this._exitRide(ride, this.mRidingExitDistance);
            this.mRidingPrevID = ride.getUniqueID();
            if (actorIsBeingDestroyed || switchingRides) {
                ride._removeRider(this.getUniqueID(), actorIsBeingDestroyed, switchingRides);
                if (actorIsBeingDestroyed) {
                    this.mRidingPrevID = ActorUniqueID.INVALID_ID;
                }
            } else {
                ride.flagRiderToRemove(this);
            }
            if (!switchingRides && !this.mRemoved) {
                if (this.getLevel().isClientSide() && this.hasCategory(ActorCategory.Player_1) && ((Player) this).isLocalPlayer()) {
                    long targetId = ride.getRuntimeID();
                    InteractPacket packet = new InteractPacket();
                    packet.setAction(InteractPacket.Action.LEAVE_VEHICLE); //StopRiding
                    packet.setRuntimeEntityId(targetId);
                    packet.setMousePosition(DataConverter.vector3f(this.getStateVectorComponent().getPos()));
                    this.getLevel().getPacketSender().send(packet);
                }
            }
        } else {
            this.mRidingID = ActorUniqueID.INVALID_ID;
        }
        this.mRidingID = ActorUniqueID.INVALID_ID;
        this.setEnforceRiderRotationLimit(false);
        //this.getLevel().getActorEventCoordinator().sendActorStopRiding(this, exitFromRider, actorIsBeingDestroyed, switchingRides);
    }

    public void rideLanded(final Vec3 rideJumpStartPos, final Vec3 rideJumpEndPos) {
    }

    @NotImplemented
    public void sendMotionPacketIfNeeded() {
    }

    @NotImplemented
    private void _sendLinkPacket(final ActorLink link) {
//        if (!this.getLevel().isClientSide()) {
//            SetActorLinkPacket packet = new SetActorLinkPacket(link);
//            this.getLevel().getPacketSender().send(packet);
//        }
    }

    @NotImplemented
    private void _updateOwnerChunk() {
//        BlockPos blockPos = new BlockPos(this.getStateVectorComponent().getPos());
//        ChunkPos cp = new ChunkPos(blockPos);
//        if (this.isInWorld() && !this.mChunkPos.equals(cp) && !this.hasCategory(ActorCategory.Player_1)) {
//            this.getDimension().flagEntityforChunkMove(this);
//        }
    }

    @NotImplemented
    public boolean drop(final ItemStack item, boolean randomly) {
        return false;
    }

    @NotImplemented
    public void addEffect(final MobEffectInstance effect) {
//        if (this.canBeAffected(effect)) {
//            int effectID = effect.getId();
//            while (this.mMobEffects.size() < effectID + 1) {
//                this.mMobEffects.add(MobEffectInstance.NO_EFFECT);
//            }
//            MobEffectInstance effectInstance = this.mMobEffects.get(effectID);
//            if (!effectInstance.equals(MobEffectInstance.NO_EFFECT)) {
//                effectInstance.update(effect);
//                this.onEffectUpdated(effectInstance);
//            }
//        }
    }

    @NotImplemented
    public void remove() {
//        DwellerComponent dweller = this.tryGetComponent<DwellerComponent>();
//        if (dweller != null)
//            dweller.onRemove(this);
//        BossComponent bossComponent = this.tryGetComponent<BossComponent>();
//        if (bossComponent != null) {
//            for (Map.Entry<UUID, PlayerListEntry> player : this.getLevel().getPlayerList()) {
//                Player playerPtr = this.getLevel().getPlayer(player.getKey());
//                if (playerPtr != null)
//                    bossComponent.unRegisterPlayer(this, playerPtr);
//            }
//        }
//        this.getLevel().getActorEventCoordinator().sendActorRemoved(this);
        this.mRemoved = true;
    }

    @NotImplemented
    public void checkBlockCollisions() {
        this.checkBlockCollisions(this.getAABBShapeComponent().getAABB());
    }

    @NotImplemented
    public void checkBlockCollisions(final AABB aabb) {
//        this.mInsideBlock = BedrockBlocks.mAir;
//        int x0 = (int) Math.floor(aabb.min.x + 0.001f);
//        int y0 = (int) Math.floor(aabb.min.y + 0.001f);
//        int z0 = (int) Math.floor(aabb.min.z + 0.001f);
//        int x1 = (int) Math.floor(aabb.max.x - 0.001f);
//        int y1 = (int) Math.floor(aabb.max.y - 0.001f);
//        int z1 = (int) Math.floor(aabb.max.z - 0.001f);
//        BlockPos pos = new BlockPos(x0, y0, z0);
//        Vec3 min = new Vec3(pos);
//        BlockPos v17 = new BlockPos(x1, y1, z1);
//        Vec3 max = new Vec3(v17);
//        AABB bb = new AABB(min, max);
//        if (this.mRegion.hasChunksAt(bb)) {
//            for (int x = x0; x <= x1; ++x ) {
//                for (int y = y0; y <= y1; ++y ) {
//                    for (int z = z0; z <= z1; ++z ) {
//                        BlockPos blockPos = new BlockPos(x, y, z);
//                        Block b = this.mRegion.getBlock(blockPos);
//                        b.entityInside(this.mRegion, blockPos, this);
//                    }
//                }
//            }
//        }
    }

    public final void reload() {
//        if (this.isRegionValid()) {
//            if (this.mDefinitionList != null) {
//                this.mCurrentDescription = this.mDefinitionList.getDescription();
//            } else {
//                this.mCurrentDescription = null;
//            }
//            if (!this.mInitialized) {
//                if (this.mForceInitMethodToSpawnOnReload) {
//                    this.mInitMethod = SPAWNED;
//                    this.mForceInitMethodToSpawnOnReload = false;
//                }
//                String eventName = "";
//                switch (this.mInitMethod) {
//                    case SPAWNED:
//                        eventName = "minecraft:entity_spawned";
//                        break;
//                    case BORN:
//                        eventName = "minecraft:entity_born";
//                        break;
//                    case TRANSFORMED:
//                        eventName = "minecraft:entity_transformed";
//                        break;
//                    case EVENT:
//                        eventName = this.mCustomInitEventName;
//                        this.mInitMethod = SPAWNED;
//                        break;
//                    default:
//                        break;
//                }
//                if (!eventName.isEmpty() && this.mCurrentDescription != null) {
//                    this.mCurrentDescription.executeEvent(this, eventName, this.mInitParams);
//                    this.updateDescription();
//                }
//            }
//            if (this.getLevel().isClientSide()) {
//                this.reloadHardcodedClient(this.mInitMethod, this.mInitParams);
//            }
//            this.mInitialized = true;
//            if (!this.getLevel().isClientSide() || this.hasCategory(ActorCategory.Player_1)) {
//                float tempFallDistance = this.mFallDistance;
//                boolean onGroundRetainer = this.mOnGround;
//                this.move(Vec3.ZERO);
//                this.mOnGround = onGroundRetainer;
//                this.mFallDistance = tempFallDistance;
//            }
//            this.mInitMethod = UPDATED;
//            this.mInitParams.clear();
//        }
    }

//    @NotImplemented
//    public void reloadHardcodedClient(Actor.InitializationMethod method, final VariantParameterList params) {
//    }

    @NotImplemented
    public final void refreshComponents() {
//        if (this.isRegionValid() && !this.mRemoved && this.mDefinitionList != null) {
//            if (this.mDefinitionList.hasChanged()) {
//                this.updateDescription();
//                this.reload();
//            }
//        }
    }

    @NotImplemented
    public final boolean tick(BlockSource region) {
        if (!ActorClassTree.isTypeInstanceOf(this.getEntityTypeId(), ActorType.Player_0) && (region == null || !region.hasBlock(new BlockPos(this.getStateVectorComponent().getPos()))) && !this.isGlobal()) {
            return false;
        }
        if (this.hasCategory(ActorCategory.Mob_0)) {
            this.getLevel().tickedMob();
        }
        if (!this.hasCategory(ActorCategory.Player_1))
            this.setRegion(region);
//        if (!this.hasTickingArea() || this.getEntity().getComponent<TickWorldComponent>().getTickingArea().getView().checkInitialLoadDone()) {
            this.refreshComponents();
            int lifetimeTicks = this.mEntityData.getInt(ActorDataIDs.DATA_LIFETIME_TICKS.ordinal());
            if (this.mHasLimitedLife != lifetimeTicks > 0) {
                this.mHasLimitedLife = lifetimeTicks > 0;
                this.mLimitedLifeTicks = lifetimeTicks;
            }
            if ( this.mHasLimitedLife && --this.mLimitedLifeTicks <= 0) {
                this.remove();
                return false;
            }
            Actor riddenEntity = this.getRide();
            if (riddenEntity == null || !riddenEntity.isControlledByLocalInstance()) {
                Vec3 pos = this.getStateVectorComponent().getPos();
                Vec3 rhs = this.getStateVectorComponent().getPosPrev();
                Vec3 delta = pos.subtract(rhs);
                this.setStatusFlag(ActorFlags.MOVING, delta.lengthSquared() > 0.0099999998);
                this._getStateVectorComponentNonConst()._setPosPrev(pos);
                this.mRotPrev = this.mRot;
            }
            this.mSwimPrev = this.mSwimAmount;
            if (this.isSwimming()) {
                this.mSwimAmount = Math.min(1.0f, this.mSwimAmount + 0.1f);
            } else {
                this.mSwimAmount = Math.max(0.0f, this.mSwimAmount + -0.1f);
            }
//            this._manageRiders(region);
            if (riddenEntity != null)
                this.rideTick();
            else
                this.normalTick();
//            this._updateOnewayCollisions(region);
            this.checkBlockCollisions();
//            this.updateTickingData();
            if (this.getLevel().isClientSide() && !this.isRiding())
                this._playMovementSound(this.mOnGround);
            this.mTeleportedThisTick = false;
            this.mPushedByID = ActorUniqueID.INVALID_ID;
//            if (this.mEntity != null) {
//                this.mEntity.get().addComponent<FlagComponent<ActorTickedFlag>>();
//            }
            return true;
//        } else {
//            return false;
//        }
    }

    public void rideTick() {
        Actor riddenEntity = this.getRide();
        if (riddenEntity != null && riddenEntity.mRemoved) {
            this.stopRiding(true, false, false);
        } else {
            this.getStateVectorComponentNonConst().mPosDelta.set(Vec3.ZERO);
            this.normalTick();
            if (riddenEntity != null) {
                boolean isUncontrolled = riddenEntity.getControllingPlayer() == ActorUniqueID.INVALID_ID;
                if (this.getLevel().getPrimaryLocalPlayer() != null) {
                    isUncontrolled = isUncontrolled || riddenEntity.getControllingPlayer() != this.getLevel().getPrimaryLocalPlayer().getUniqueID();
                }
                if (isUncontrolled && !this.hasCategory(ActorCategory.Player_1))
                    riddenEntity.positionRider(this, 0.0f);
            }
        }
    }

    @NotImplemented
    public void normalTick() {
    }

    @NotImplemented
    public void positionRider(Actor rider, float a) {
    }

    @NotImplemented
    private void _playMovementSound(boolean onGround) {
    }

//----------------------------------------------------------------------------------------------------------------------

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }
}
