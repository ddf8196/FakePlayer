package com.ddf.fakeplayer.util.mc;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.util.Vec3;

public class HitResult {
    private final Vec3 mStartPos;
    private final Vec3 mRayDir;
    private HitResultType mType;
    private /*FacingID*/int mFacing;
    private final BlockPos mBlock;
    private final Vec3 mPos;
    private Actor mEntity;
    private boolean mIsHitLiquid;
    private /*FacingID*/int mLiquidFacing;
    private final BlockPos mLiquid;
    private final Vec3 mLiquidPos;
    private boolean mIndirectHit;

    public HitResult() {
        this(new Vec3(Vec3.ZERO), new Vec3(Vec3.ZERO), new Vec3(Vec3.ZERO));
    }

    public HitResult(final HitResult a2) {
        this.mStartPos = new Vec3(a2.mStartPos);
        this.mRayDir = new Vec3(a2.mRayDir);
        this.mType = a2.mType;
        this.mFacing = a2.mFacing;
        this.mBlock = new BlockPos(a2.mBlock);
        this.mPos = a2.mPos;
        this.mEntity = a2.mEntity;
        this.mIsHitLiquid = a2.mIsHitLiquid;
        this.mLiquid = new BlockPos(a2.mLiquid);
        this.mLiquidPos = new Vec3(a2.mLiquidPos);
        this.mIndirectHit = a2.mIndirectHit;
    }

    public HitResult(final Vec3 startPos, final Vec3 rayDir, final Vec3 rayEnd) {
        this.mStartPos = startPos;
        this.mRayDir = rayDir;
        this.mType = HitResultType.NO_HIT;
        this.mFacing = 0;
        this.mBlock = new BlockPos(BlockPos.ZERO);
        this.mPos = rayEnd;
        this.mEntity = null;
        this.mIsHitLiquid = false;
        this.mLiquid = new BlockPos(BlockPos.ZERO);
        this.mLiquidPos = new Vec3(Vec3.ZERO);
        this.mIndirectHit = false;
    }

    public final BlockPos getLiquid() {
        return this.mLiquid;
    }

    public final /*FacingID*/int getLiquidFacing() {
        return this.mLiquidFacing;
    }

    public final Vec3 getLiquidPos() {
        return this.mLiquidPos;
    }

    public final boolean isHitLiquid() {
        return this.mIsHitLiquid;
    }

    public final float distanceTo(final Actor otherEntity) {
        Vec3 otherEntityPos = otherEntity.getStateVectorComponent().getPos();
        float xd = this.mPos.x - otherEntityPos.x;
        float yd = this.mPos.y - otherEntityPos.y;
        float zd = this.mPos.z - otherEntityPos.z;
        return (xd * xd) + (yd * yd) + (zd * zd);
    }
}
