package com.ddf.fakeplayer.actor.component;

import com.ddf.fakeplayer.util.AABB;
import com.ddf.fakeplayer.util.Vec2;

public class AABBShapeComponent {
    private AABB mAABB;
    private Vec2 mBBDim;

    public AABBShapeComponent() {
        this.mAABB = new AABB();
        this.mBBDim = new Vec2(0.60000002f, 1.8f);
    }

    public final AABB getAABB() {
        return this.mAABB;
    }
    
    public final Vec2 getAABBDim() {
        return this.mBBDim;
    }

    public final void setAABB(final AABB aabb) {
        this.mAABB = aabb;
    }

    public final void setAABBDim(final Vec2 aabbDim) {
        this.mBBDim = aabbDim;
    }
}
