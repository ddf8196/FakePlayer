package com.ddf.fakeplayer.js.functions;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class BaseFunction extends org.mozilla.javascript.BaseFunction {
    public BaseFunction() {}

    public BaseFunction(Scriptable scope) {
        super(scope, ScriptableObject.getFunctionPrototype(scope));
    }

    public BaseFunction(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public void install(Scriptable scope) {
        ScriptableObject.putProperty(scope, getFunctionName(), this);
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw new UnsupportedOperationException();
    }
}
