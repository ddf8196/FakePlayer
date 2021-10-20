package com.ddf.fakeplayer.js.classes.entity.effect;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;

public class JsEffect extends BaseScriptableObject {
    public JsEffect() {}

    public JsEffect(Scriptable scope) {
        this(scope, getPrototype(JsEffect.class, scope));
    }

    public JsEffect(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsEffect.class, scope, prototype);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public int amplifier() {
        return 0;
    }

    @JSGetter
    public String displayName() {
        return "";
    }

    @JSGetter
    public int duration() {
        return 0;
    }
}
