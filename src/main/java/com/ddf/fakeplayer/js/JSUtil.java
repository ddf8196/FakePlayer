package com.ddf.fakeplayer.js;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class JSUtil {
    public static Function tryGetFunction(Scriptable scope, String functionName) {
        Object function = scope.get(functionName, scope);
        if (function instanceof Function) {
            return (Function) function;
        }
        return null;
    }
}
