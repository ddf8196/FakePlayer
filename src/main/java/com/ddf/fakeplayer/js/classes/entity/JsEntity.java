package com.ddf.fakeplayer.js.classes.entity;

import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.classes.entity.effect.JsEffect;
import com.ddf.fakeplayer.js.classes.entity.effect.JsEffectType;
import com.ddf.fakeplayer.js.classes.location.JsLocation;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;

public class JsEntity extends BaseScriptableObject {
    private Actor actor;

    public JsEntity() {}

    public JsEntity(Scriptable scope) {
        super(scope, getPrototype(JsEntity.class, scope));
    }

    public JsEntity(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsEntity.class, scope, prototype);
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public String id() {
        return "";
    }

    @JSGetter
    public boolean isSneaking() {
        return false;
    }

    @JSSetter
    public void isSneaking(boolean isSneaking) {

    }

    @JSGetter
    public JsLocation location() {
        return null;
    }

    @JSGetter
    public String nameTag() {
        return null;
    }

    @JSSetter
    public void nameTag(String nameTag) {
    }

    @JSGetter
    public JsLocation velocity() {
        return null;
    }

    @JSFunction
    public void addEffect(JsEffectType effectType, int duration, int amplifier) {
    }

    @JSFunction
    public Object getComponent(String componentId) {
        return null;
    }

    @JSFunction
    public Object[] getComponents() {
        return new Object[0];
    }

    @JSFunction
    public JsEffect getEffect(JsEffectType effectType) {
        return null;
    }

    @JSFunction
    public boolean hasComponent(String componentId) {
        return false;
    }

    @JSFunction
    public void kill() {
    }

    @JSFunction
    public void triggerEvent(String eventName) {
    }
}
