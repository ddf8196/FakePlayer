package com.ddf.fakeplayer.js.classes.entity.player;

import com.ddf.fakeplayer.actor.player.FakePlayer;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.util.Vec2;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;

public class JsScriptPlayerHeadRotation extends BaseScriptableObject {
    private FakePlayer fakePlayer;
    private double pitch, yaw;

    public JsScriptPlayerHeadRotation() {}

    public JsScriptPlayerHeadRotation(Scriptable scope) {
        this(scope, getPrototype(JsScriptPlayerHeadRotation.class, scope));
    }

    public JsScriptPlayerHeadRotation(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsScriptPlayerHeadRotation.class, scope, prototype);
    }

    public FakePlayer getFakePlayer() {
        return fakePlayer;
    }

    public void setFakePlayer(FakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public double pitch() {
        return fakePlayer.getRotation().x;
    }

    @JSSetter
    public void pitch(double pitch) {
        fakePlayer.setRot(new Vec2((float) pitch, fakePlayer.getRotation().y));
    }

    @JSGetter
    public double yaw() {
        return fakePlayer.getYHeadRot();
    }

    @JSSetter
    public void yaw(double yaw) {
        fakePlayer.setYHeadRot((float) yaw);
    }
}
