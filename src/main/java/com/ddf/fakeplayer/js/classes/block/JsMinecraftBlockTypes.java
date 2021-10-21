package com.ddf.fakeplayer.js.classes.block;

import com.ddf.fakeplayer.block.BlockLegacy;
import com.ddf.fakeplayer.block.VanillaBlockTypes;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.util.JSUtil;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsMinecraftBlockTypes extends BaseScriptableObject {
    public static final Map<String, BlockLegacy> blocks = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        blocks.put("concrete", VanillaBlockTypes.mConcrete);
        blocks.put("concretepowder", VanillaBlockTypes.mConcretePowder);
        blocks.put("noteblock", VanillaBlockTypes.mNote);
        blocks.put("sapling", VanillaBlockTypes.mSapling);
        blocks.put("wool", VanillaBlockTypes.mWool);
        blocks.put("soulsand", VanillaBlockTypes.mSoulSand);
        blocks.put("skull", VanillaBlockTypes.mSkull);
    }

    public JsMinecraftBlockTypes() {}

    public JsMinecraftBlockTypes(Scriptable scope) {
        this(scope, getPrototype(JsMinecraftBlockTypes.class, scope));
    }

    public JsMinecraftBlockTypes(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsMinecraftBlockTypes.class, scope, prototype);
        blocks.forEach((name, block) -> {
            JsBlockType blockType = JSUtil.newObject(JsBlockType.class, scope);
            blockType.setBlockLegacy(block);
            ScriptableObject.defineProperty(constructor, name, blockType, ScriptableObject.PERMANENT | ScriptableObject.READONLY | ScriptableObject.DONTENUM);
        });
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSStaticFunction
    public static JsBlockType get(String typeName) {
        return null;
    }

    @JSStaticFunction
    public static JsBlockType[] getAllBlockTypes() {
        return new JsBlockType[0];
    }
}
