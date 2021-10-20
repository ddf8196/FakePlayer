package com.ddf.fakeplayer.js.functions.fs;

import com.ddf.fakeplayer.js.functions.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class WriteFileSync extends BaseFunction {
    public WriteFileSync(Scriptable scope) {
        super(scope);
    }

    @Override
    public String getFunctionName() {
        return "writeFileSync";
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return super.call(cx, scope, thisObj, args);
    }
}