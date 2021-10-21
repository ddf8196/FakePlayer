package com.ddf.fakeplayer.actor.component;

import com.ddf.fakeplayer.util.Vec3;

public class StateVectorComponent {
    private Vec3 mPos = new Vec3(Vec3.ZERO);
    private Vec3 mPosPrev = new Vec3(Vec3.ZERO);
    public Vec3 mPosDelta = new Vec3(Vec3.ZERO);

    public void _setPos(final Vec3 pos) {
        this.mPos = pos;
    }

    public void _setPosPrev(final Vec3 posPrev) {
        this.mPosPrev = posPrev;
    }

    public final Vec3 getPos() {
        return this.mPos;
    }

    public final Vec3 getPosDelta() {
        return this.mPosDelta;
    }

    public Vec3 getPosPrev() {
        return this.mPosPrev;
    }

    public void setPos(Vec3 mPos) {
        this.mPos = mPos;
    }

    public void setPosPrev(Vec3 mPosPrev) {
        this.mPosPrev = mPosPrev;
    }

    public void setPosDelta(Vec3 mPosDelta) {
        this.mPosDelta = mPosDelta;
    }
}
