package com.ddf.fakeplayer.actor.mob;

import com.ddf.fakeplayer.actor.*;
import com.ddf.fakeplayer.actor.attribute.AttributeInstance;
import com.ddf.fakeplayer.actor.attribute.AttributeOperands;
import com.ddf.fakeplayer.actor.attribute.HealthAttributeDelegate;
import com.ddf.fakeplayer.actor.attribute.SharedAttributes;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.container.slot.EquipmentSlot;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Vec3;

import java.util.Map;

@SuppressWarnings("all")
public class Mob extends Actor {
    private float mYBodyRot = 0.0f;
    private float mYBodyRotO = 0.0f;
    private float mYHeadRot = 0.0f;
    private float mYHeadRotO = 0.0f;
    private int mHurtTime = 0;
    private int mHurtDuration = 0;
    private float mHurtDir = 0.0f;
    private int mAttackTime = 0;
    private float mOTilt = 0;
    private float mTilt = 0;
    private int mLookTime = 0;
    private float mFallTime = 0.0f;
    private boolean mFloatsInLiquid = true;
    private int mJumpTicks = 0;
    private Vec3 mElytraRot = new Vec3();
    //private CompassSpriteCalculator mCompassSpriteCalc = new CompassSpriteCalculator();
    //private ClockSpriteCalculator mClockSpriteCalc = new ClockSpriteCalculator();
    private float mXxa = 0.0f;
    private float mYya = 0.0f;
    private float mZza = 0.0f;
    private float mYRotA = 0.0f;
    private boolean mHasMoveInput = false;
    private float mAttackAnim = 0.0f;
    private float mORun = 0.0f;
    private float mRun = 0.0f;
    private boolean mSwinging = false;
    private int mSwingTime = 0;
    private int mNoActionTime = 0;
    private int mNoJumpDelay = 0;
    private float mDefaultLookAngle = 0.0f;
    private float mFrictionModifier = 1.0f;
    private float mFlyingSpeed = 0.02f;
    protected float mSwimSpeedMultiplier = 1.0f;
    protected int mDeathTime = 0;
    private int mDeathScore = 0;
    private float mAnimStep = 0.0f;
    private float mAnimStepO = 0.0f;
    private float mLockedBodyYRot = 0.0f;
    private float mRiderYRotLimit = 181.0f;
    //private MovementInterpolator mInterpolation = new MovementInterpolator();
    private int mLastHurt = 0;
    //private ActorDamageCause mLastHurtCause = None_18;
    private int mDmgSpill = 0;
    private int mDmgPartial = 0;
    private boolean mJumping = false;
    private boolean mJumpVelRedux = false;
    private float mPlayerJumpPendingScale = 0.0f;
    private boolean mAllowStandSliding = false;
    private Vec3 mJumpStartPos = new Vec3();
    private float mSpeed = 0.0f;
    private boolean mSurfaceMob = false;
    private boolean mNaturallySpawned = false;
    protected boolean mDead = false;
    //private VillageLegacy mVillageLegacy;
    private boolean mWantsToBeJockey = false;
    private int mAmbientPlayBackInterval = 2000;
    protected boolean mSpawnedXP = false;
    private int mRollCounter = 0;
    private /*ActorUniqueID*/long mLookingAtId = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mLastHurtMobId = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mLastHurtByMobId = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mLastHurtByPlayerId = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mCaravanHead = ActorUniqueID.INVALID_ID;
    private /*ActorUniqueID*/long mCaravanTail = ActorUniqueID.INVALID_ID;
    private int mLastHurtMobTimestamp = 0;
    private int mLastHurtByMobTime = 0;
    private float mOAttackAnim = 0.0f;
    private int mArrowCount = 0;
    private int mRemoveArrowTime = 0;
    private int mFallFlyTicks = 0;
    private boolean mHasBoundOrigin = false;
    private BlockPos mBoundOrigin = new BlockPos();
    //private MobSpawnMethod mSpawnMethod = Unknown_32;
    private boolean mCreateAiOnReload = true;
    private /*ActorUniqueID*/long mTargetCaptainId = ActorUniqueID.INVALID_ID;

    @NotImplemented
    public Mob(Level level) {
        super(level);
        this.mCategories |= ActorCategory.Mob_0.getValue();
//        this.enableAutoSendPosRot(1);
//        _defineMobData(this.mEntityData);
        this.registerAttributes();
//        this._initHardCodedComponents();
        this.mMaxAutoStep = 0.5625f;
    }

    private void registerAttributes() {
        AttributeInstance health = this.mAttributes.registerAttribute(SharedAttributes.HEALTH);
        health.setDelegate(new HealthAttributeDelegate(health, this));
        health.setDefaultValue(20.0f, AttributeOperands.OPERAND_CURRENT);
        this.mAttributes.registerAttribute(SharedAttributes.ABSORPTION).setMaxValue(16.0f);
        this.mAttributes.registerAttribute(SharedAttributes.KNOCKBACK_RESISTANCE).setDefaultValue(1.0f, AttributeOperands.OPERAND_MAX);
        this.mAttributes.registerAttribute(SharedAttributes.MOVEMENT_SPEED).setDefaultValue(0.69999999f, AttributeOperands.OPERAND_CURRENT);
        this.mAttributes.registerAttribute(SharedAttributes.UNDERWATER_MOVEMENT_SPEED).setDefaultValue(0.02f, AttributeOperands.OPERAND_CURRENT);
        this.mAttributes.registerAttribute(SharedAttributes.LUCK).setRange(-1024.0f, 0.0f, 1024.0f);
        this.mAttributes.registerAttribute(SharedAttributes.FOLLOW_RANGE).setRange(0.0f, 16.0f, 2048.0f);
        this.resetAttributes();
    }

    public void resetAttributes() {
        for (Map.Entry<Integer, AttributeInstance> i : this.mAttributes) {
            i.getValue().resetToDefaultValue();
        }
    }

    public final boolean isGliding() {
        return this.getStatusFlag(ActorFlags.GLIDING);
    }

    public boolean isImmobile() {
        if (!this.mImmobile) {
            if (!this.getStatusFlag(ActorFlags.NOAI) && this.getHealth() <= 0) {
                return !this.mIsKnockedBackOnDeath;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isAlive() {
        if (!this.isRemoved())
            return this.getHealth() > 0;
        return false;
    }

    @Override
    public float getYHeadRot() {
        return this.mYHeadRot;
    }

    @NotImplemented
    public final int getCurrentSwingDuration() {
        return 6;
//        int amp = 0;
//        if (this.hasEffect(MobEffect.DIG_SPEED)) {
//            amp = MobEffectInstance.getAmplifier(this.getEffect(MobEffect.DIG_SPEED));
//        }
//        if (this.hasEffect(MobEffect.CONDUIT_POWER)) {
//            amp = Math.max(amp, this.getEffect(MobEffect.CONDUIT_POWER).getAmplifier() + 1);
//        }
//        if (amp > 0)
//            return 6 - (amp + 1);
//        if (!this.hasEffect(MobEffect.DIG_SLOWDOWN))
//            return 6;
//        return 2 * (this.getEffect(MobEffect.DIG_SLOWDOWN).getAmplifier() + 1) + 6;
    }

    public final ItemStack getItemSlot(EquipmentSlot slot) {
        if (slot.isHandSlot()) {
            return this.mHand.getItem(slot.toSlot());
        } else if (slot.isArmorSlot()) {
            return this.mArmor.getItem(slot.toSlot());
        } else {
            return ItemStack.EMPTY_ITEM;
        }
    }

    public final void setJumping(boolean jump) {
        this.mJumping = jump;
    }

    public void setYHeadRot(float yHeadRot) {
        this.mYHeadRot = yHeadRot;
    }

    public void setSleeping(boolean val) {
        super.setStatusFlag(ActorFlags.SLEEPING, val);
    }

    @Override
    public void swing() {
        if (!this.mSwinging || (this.mSwingTime >= this.getCurrentSwingDuration() / 2) || this.mSwingTime < 0 ) {
            this.mSwingTime = -1;
            this.mSwinging = true;
        }
    }

    @NotImplemented
    @Override
    public void teleportTo(final Vec3 pos, boolean shouldStopRiding, int cause, int sourceEntityType) {
        super.teleportTo(pos, shouldStopRiding, 0, ActorType.Undefined_2.getValue());
        //this.mInterpolation.reset();
    }

    @Override
    public void stopRiding(boolean exitFromRider, boolean entityIsBeingDestroyed, boolean switchingRides) {
        this.mRiderYRotLimit = 181.0f;
        super.stopRiding(exitFromRider, entityIsBeingDestroyed, switchingRides);
    }

    @Override
    public void _removeRider(final /*ActorUniqueID*/long id, boolean entityIsBeingDestroyed, boolean switchingRides) {
        boolean wasUnderLocalControl = this.isControlledByLocalInstance();
        super._removeRider(id, entityIsBeingDestroyed, switchingRides);
        if (wasUnderLocalControl && !this.isControlledByLocalInstance())
            this._endJump();
    }

    public void _endJump() {
        this.setJumpDuration(0);
        this.setJumping(false);
        if (this.hasRider()) {
            for (long riderId : this.mRiderIDs) {
                Actor rider = this.getLevel().fetchEntity(riderId, false);
                if (rider != null) {
                    rider.rideLanded(this.mJumpStartPos, this.getPos());
                }
            }
        }
    }

    @NotImplemented
    @Override
    public void normalTick() {
        super.normalTick();
    }
}
