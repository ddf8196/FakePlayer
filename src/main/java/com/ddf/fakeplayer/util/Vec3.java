package com.ddf.fakeplayer.util;

import com.ddf.fakeplayer.block.BlockPos;

public class Vec3 {
    public static final Vec3 ZERO = new Vec3(0);

    public float x, y, z;

    public Vec3() {
        this(0.0f);
    }

    public Vec3(float xyz) {
        x = y = z = xyz;
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(final Vec3 vec3) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
    }

    public Vec3(final BlockPos pos) {
        this.x = (float) pos.x;
        this.y = (float) pos.y;
        this.z = (float) pos.z;
    }

    public final Vec3 set(Vec3 vec3) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        return this;
    }

    public final float length() {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public final float lengthSquared() {
        return (this.x * this.x) + (this.y * this.y) + (this.z * this.z);
    }

    public final Vec3 add(float f) {
        return new Vec3(this.x + f, this.y + f, this.z + f);
    }

    public final Vec3 add(float x_, float y_, float z_) {
        return new Vec3(this.x + x_, this.y + y_, this.z + z_);
    }

    public final Vec3 addAndSet(Vec3 vec3) {
        return addAndSet(vec3.x, vec3.y, vec3.z);
    }

    public final Vec3 addAndSet(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public final Vec3 subtract(final float f) {
        return new Vec3(this.x - f, this.y - f, this.z - f);
    }

    public final Vec3 subtract(final Vec3 rhs) {
        return new Vec3(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z);
    }

    public final Vec3 multiplyAndSet(float k) {
        this.x = k * this.x;
        this.y = k * this.y;
        this.z = k * this.z;
        return this;
    }

    public final boolean equals(final Vec3 rhs) {
        return this.x == rhs.x
                && this.y == rhs.y
                && this.z == rhs.z;
    }
}
