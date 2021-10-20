package com.ddf.fakeplayer.js.classes;

import com.ddf.fakeplayer.util.Logger;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;

public class Console extends BaseScriptableObject {
    public Console() {}

    public Console(Scriptable scope) {
        super(scope, getPrototype(Console.class, scope));
    }

    public Console(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(Console.class, scope, prototype);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSFunction
    public void log(Object data) {
        Logger.getLogger().log(data);
    }

    @JSFunction
    public void debug(Object data) {
        log(data);
    }

    @JSFunction
    public void info(Object data) {
        log(data);
    }

    @JSFunction
    public void error(Object data) {
        log(data);
    }

    @JSFunction
    public void trace(Object data) {
        log(data);
    }

    @JSFunction
    public void warn(Object data) {
        log(data);
    }
}