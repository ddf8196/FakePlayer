package com.ddf.fakeplayer.actor.component;

import com.ddf.fakeplayer.util.Vec3;

public class StateVectorComponent {
    private Vec3 mPos = new Vec3(Vec3.ZERO);
    private Vec3 mPosPrev = new Vec3(Vec3.ZERO);
    public Vec3 mPosDelta = new Vec3(Vec3.ZERO);

    public void _setPos(final Vec3 pos) {
        this.mPos.set(pos);
    }

    public void _setPosPrev(final Vec3 posPrev) {
        this.mPosPrev.set(posPrev);
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
}
