package com.ddf.fakeplayer.js.classes.entity.component;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.classes.container.JsContainer;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;

public class JsEntityInventoryComponent extends BaseScriptableObject {
    public JsEntityInventoryComponent() {}

    public JsEntityInventoryComponent(Scriptable scope) {
        this(scope, getPrototype(JsEntityInventoryComponent.class, scope));
    }

    public JsEntityInventoryComponent(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsEntityInventoryComponent.class, scope, prototype);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public int additionalSlotsPerStrength() {
        return 0;
    }

    @JSGetter
    public boolean canBeSiphonedFrom() {
        return false;
    }

    @JSGetter
    public JsContainer container() {
        return null;
    }

    @JSGetter
    public String containerType() {
        return "";
    }

    @JSGetter
    public String id() {
        return "";
    }

    @JSGetter
    public int inventorySize() {
        return 0;
    }

    //@JSGetter
    public int jsGet_private() {
        return 0;
    }

    @JSGetter
    public boolean restrictToOwner() {
        return false;
    }
}
