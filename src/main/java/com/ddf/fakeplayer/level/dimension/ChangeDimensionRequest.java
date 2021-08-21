package com.ddf.fakeplayer.level.dimension;

import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.Vec3;

public class ChangeDimensionRequest {
    public ChangeDimensionRequest.State mState;
    public int mFromDimensionId;
    public int mToDimensionId;
    public Vec3 mPosition;
    public boolean mUsePortal;
    public boolean mRespawn;
    public CompoundTag mAgentTag;

    public ChangeDimensionRequest(int fromId, int toId, final Vec3 position, boolean usePortal, boolean respawn) {
        this.mState = State.PrepareRegion;
        this.mFromDimensionId = fromId;
        this.mToDimensionId = toId;
        this.mPosition = position;
        this.mUsePortal = usePortal;
        this.mRespawn = respawn;
        this.mAgentTag = null;
    }

    public enum State {
        PrepareRegion,
        WaitingForChunks,
        WaitingForRespawn,
    }
}
