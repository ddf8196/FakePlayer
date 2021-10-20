package com.ddf.fakeplayer.js.classes.location;

import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.util.ConstructableInJS;
import com.ddf.fakeplayer.js.util.JSUtil;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;

@ConstructableInJS
public class JsBlockLocation extends BaseScriptableObject {
    private int x, y, z;

    public JsBlockLocation() {}

    @JSConstructor
    public JsBlockLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsBlockLocation that = (JsBlockLocation) o;
        return x == that.x && y == that.y && z == that.z;
    }

    public static JsBlockLocation fromBlockPos(BlockPos pos, Scriptable scope) {
        return JSUtil.newObject(JsBlockLocation.class, scope, pos.x, pos.y, pos.z);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSGetter
    public int x() {
        return x;
    }

    @JSSetter
    public void x(int x) {
        this.x = x;
    }

    @JSGetter
    public int y() {
        return y;
    }

    @JSSetter
    public void y(int y) {
        this.y = y;
    }

    @JSGetter
    public int z() {
        return z;
    }

    @JSSetter
    public void z(int z) {
        this.z = z;
    }

    @JSFunction
    public JsBlockLocation above() {
        return new JsBlockLocation(x, y + 1, z);
    }

    @JSFunction
    public JsBlockLocation[] blocksBetween(JsBlockLocation other) {
        int maxX = Math.max(x, other.x);
        int maxY = Math.max(y, other.y);
        int maxZ = Math.max(z, other.z);

        int minX = Math.min(x, other.x);
        int minY = Math.min(y, other.y);
        int minZ = Math.min(z, other.z);

        int xLen = maxX - minX + 1;
        int yLen = maxY - minY + 1;
        int zLen = maxZ - minZ + 1;

        JsBlockLocation[] array = new JsBlockLocation[xLen * yLen * zLen];

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    array[(x * zLen + z) * yLen + y] = new JsBlockLocation(x, y, z);
                }
            }
        }
        return array;
    }

    @JSFunction
    public JsBlockLocation offset(int x, int y, int z) {
        return new JsBlockLocation(this.x + x, this.y + y, this.z + z);
    }

    @JSFunction
    public boolean equals(JsBlockLocation other) {
        return equals((Object) other);
    }
}
