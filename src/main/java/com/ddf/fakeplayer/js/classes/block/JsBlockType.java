package com.ddf.fakeplayer.js.classes.block;

import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

public class JsBlockType extends BaseScriptableObject {
    private BlockLegacy blockLegacy;

    public JsBlockType() {}

    public JsBlockType(Scriptable scope) {
        this(scope, getPrototype(JsBlockType.class, scope));
    }

    public JsBlockType(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsBlockType.class, scope, prototype);
    }

    public BlockLegacy getBlockLegacy() {
        return blockLegacy;
    }

    public void setBlockLegacy(BlockLegacy blockLegacy) {
        this.blockLegacy = blockLegacy;
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public boolean canBeWaterlogged() {
        return false;
    }

    @JSGetter
    public String id() {
        return "";
    }

    @JSFunction
    public JsBlockPermutation createDefaultBlockPermutation() {
        return null;
    }
}
