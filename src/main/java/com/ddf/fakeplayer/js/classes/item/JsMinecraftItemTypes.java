package com.ddf.fakeplayer.js.classes.item;

import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.item.VanillaItems;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.util.JSUtil;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsMinecraftItemTypes extends BaseScriptableObject {
    public static final Map<String, Item> items = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        items.put("apple", VanillaItems.mApple);
        items.put("beef", VanillaItems.mBeef_raw);
        items.put("cookedBeef", VanillaItems.mBeef_cooked);

//        items.put("noteblock", VanillaItems.mB)
    }

    public JsMinecraftItemTypes() {}

    public JsMinecraftItemTypes(Scriptable scope) {
        this(scope, getPrototype(JsMinecraftItemTypes.class, scope));
    }

    public JsMinecraftItemTypes(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsMinecraftItemTypes.class, scope, prototype);
        items.forEach((name, item) -> {
            JsItemType itemType = JSUtil.newObject(JsItemType.class, scope);
            itemType.setItem(item);
            ScriptableObject.defineProperty(constructor, name, item, ScriptableObject.PERMANENT | ScriptableObject.READONLY | ScriptableObject.DONTENUM);
        });
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }
}
