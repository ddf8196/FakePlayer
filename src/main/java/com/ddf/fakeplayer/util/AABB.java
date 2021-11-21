package com.ddf.fakeplayer.util;

public class AABB {
    public Vec3 min, max;
    public boolean empty;

    public AABB() {
        this.min = new Vec3(0.0f);
        this.max = new Vec3(1.0f);
        this.empty = false;
    }

    public AABB(final Vec3 min, final Vec3 max) {
        this.min = min;
        this.max = max;
        this.empty = min.equals(Vec3.ZERO) && max.equals(Vec3.ZERO);
    }

    public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.min = new Vec3(minX, minY, minZ);
        this.max = new Vec3(maxX, maxY, maxZ);
        this.empty = min.equals(Vec3.ZERO) && max.equals(Vec3.ZERO);
    }

    public AABB(AABB aabb) {
        this.min = aabb.min.clone();
        this.max = aabb.max.clone();
        this.empty = aabb.empty;
    }

    public final boolean isEmpty() {
        return this.empty;
    }

    public void set(AABB aabb) {
        this.min.set(aabb.min);
        this.max.set(aabb.max);
        this.empty = aabb.empty;
    }

    @Override
    public AABB clone() {
        return new AABB(this);
    }
}
