package com.ddf.fakeplayer.js.classes.entity.player;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.classes.location.JsBlockLocation;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;

public class JsScriptNavigationResult extends BaseScriptableObject {
    public JsScriptNavigationResult() {}

    public JsScriptNavigationResult(Scriptable scope) {
        super(scope, getPrototype(JsScriptNavigationResult.class, scope));
    }

    public JsScriptNavigationResult(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsScriptNavigationResult.class, scope, prototype);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public boolean isFullPath() {
        return false;
    }

    @JSGetter
    public JsBlockLocation[] path() {
        return new JsBlockLocation[0];
    }
}
