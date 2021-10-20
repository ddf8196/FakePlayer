package com.ddf.fakeplayer.js.functions.fs;

import com.ddf.fakeplayer.js.JSLoader;
import com.ddf.fakeplayer.js.functions.BaseFunction;
import com.ddf.fakeplayer.js.util.JSUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;

import java.nio.file.Files;

public class ReadFile extends BaseFunction {
    public ReadFile(Scriptable scope) {
        super(scope);
    }

    @Override
    public String getFunctionName() {
        return "readFile";
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return Undefined.instance;
    }
}
