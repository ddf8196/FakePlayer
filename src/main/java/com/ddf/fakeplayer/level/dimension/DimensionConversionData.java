package com.ddf.fakeplayer.level.dimension;

import com.ddf.fakeplayer.util.Vec3;

public class DimensionConversionData {
    private Vec3 mOverworldSpawnPoint;
    private int mNetherScale;

    public DimensionConversionData(final Vec3 overworldSpawnPoint, int mNetherScale) {
        this.mOverworldSpawnPoint = overworldSpawnPoint;
        this.mNetherScale = mNetherScale;
    }

    public int getNetherScale() {
        return this.mNetherScale;
    }

    public Vec3 getOverworldSpawnPoint() {
        return this.mOverworldSpawnPoint;
    }
}
