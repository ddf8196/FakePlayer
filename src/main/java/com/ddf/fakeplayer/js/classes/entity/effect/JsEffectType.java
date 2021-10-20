package com.ddf.fakeplayer.js.classes.entity.effect;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;

public class JsEffectType extends BaseScriptableObject {
    public JsEffectType() {}

    public JsEffectType(Scriptable scope) {
        this(scope, getPrototype(JsEffectType.class, scope));
    }

    public JsEffectType(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsEffectType.class, scope, prototype);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSFunction
    public String getName() {
        return "";
    }
}
