package com.ddf.fakeplayer.js.classes.entity.effect;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;

public class JsMinecraftEffectTypes extends BaseScriptableObject {
    public JsMinecraftEffectTypes() {}

    public JsMinecraftEffectTypes(Scriptable scope) {
        this(scope, getPrototype(JsMinecraftEffectTypes.class, scope));
    }

    public JsMinecraftEffectTypes(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsMinecraftEffectTypes.class, scope, prototype);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }
}
