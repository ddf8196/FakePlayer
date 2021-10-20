package com.ddf.fakeplayer.js.classes.location;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.util.ConstructableInJS;
import com.ddf.fakeplayer.js.util.JSUtil;
import com.ddf.fakeplayer.util.Vec3;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;

@ConstructableInJS
public class JsLocation extends BaseScriptableObject {
    private double x, y, z;

    public JsLocation() {}

    @JSConstructor
    public JsLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsLocation location = (JsLocation) o;
        return Double.compare(location.x, x) == 0 && Double.compare(location.y, y) == 0 && Double.compare(location.z, z) == 0;
    }

    public static JsLocation fromVec3(Vec3 vec3, Scriptable scope) {
        return JSUtil.newObject(JsLocation.class, scope, (double) vec3.x, (double) vec3.y, (double) vec3.z);
    }

    public Vec3 toVec3() {
        return new Vec3((float) x, (float) y, (float) z);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSGetter
    public double x() {
        System.out.println("Location_x");
        return x;
    }

    @JSSetter
    public void x(double x) {
        this.x = x;
    }

    @JSGetter
    public double y() {
        return y;
    }

    @JSSetter
    public void y(double y) {
        this.y = y;
    }

    @JSGetter
    public double z() {
        return z;
    }

    @JSSetter
    public void z(double z) {
        this.z = z;
    }

    @JSFunction
    public boolean isNear(JsLocation other, double epsilon) {
        return epsilon >= Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z));
    }

    @JSFunction
    public boolean equals(JsLocation other) {
        return equals((Object) other);
    }
}
