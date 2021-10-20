package com.ddf.fakeplayer.js.functions.fs;

import com.ddf.fakeplayer.js.functions.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class WriteFile extends BaseFunction {
    public WriteFile(Scriptable scope) {
        super(scope);
    }

    @Override
    public String getFunctionName() {
        return "writeFile";
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return super.call(cx, scope, thisObj, args);
    }
}