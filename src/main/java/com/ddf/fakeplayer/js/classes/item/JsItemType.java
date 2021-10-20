package com.ddf.fakeplayer.js.classes.item;

import com.ddf.fakeplayer.item.Item;
import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;

public class JsItemType extends BaseScriptableObject {
    private Item item;

    public JsItemType() {}

    public JsItemType(Scriptable scope) {
        this(scope, getPrototype(JsItemType.class, scope));
    }

    public JsItemType(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsItemType.class, scope, prototype);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSFunction
    public String getName() {
        return item.getRawNameId();
    }
}
