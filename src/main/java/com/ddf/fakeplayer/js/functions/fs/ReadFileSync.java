package com.ddf.fakeplayer.js.functions.fs;

import com.ddf.fakeplayer.js.JSLoader;
import com.ddf.fakeplayer.js.functions.BaseFunction;
import com.ddf.fakeplayer.js.util.JSUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;

import java.nio.file.Files;

public class ReadFileSync extends BaseFunction {
    public ReadFileSync(Scriptable scope) {
        super(scope);
    }

    @Override
    public String getFunctionName() {
        return "readFileSync";
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        try {
            switch (args.length) {
                case 1: {
                    String path = JSUtil.tryCastArgument(args[0], String.class);
                    byte[] bytes = Files.readAllBytes(JSLoader.getScriptsDir().resolve(path));
                    NativeArrayBuffer arrayBuffer = (NativeArrayBuffer) cx.newObject(scope, "ArrayBuffer", new Object[] {bytes.length});
                    System.arraycopy(bytes, 0, arrayBuffer.getBuffer(), 0, bytes.length);
                    return arrayBuffer;
                }
                case 2: {
                    String path = JSUtil.tryCastArgument(args[0], String.class);
                    String options = JSUtil.tryCastArgument(args[1], String.class);
                    break;
                }
                default:
                    throw new IllegalArgumentException();
            }
        } catch (Throwable t) {
            return Undefined.instance;
        }
        return super.call(cx, scope, thisObj, args);
    }
}