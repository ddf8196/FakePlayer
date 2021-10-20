package com.ddf.fakeplayer.js.classes.container;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.classes.item.JsItemStack;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

public class JsContainer extends BaseScriptableObject {
    public JsContainer() {}

    public JsContainer(Scriptable scope) {
        this(scope, getPrototype(JsContainer.class, scope));
    }

    public JsContainer(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public static void finishInit(Scriptable scope, FunctionObject constructor, Scriptable prototype) {
        putPrototype(JsContainer.class, scope, prototype);
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSConstructor
    public void constructor() {
        throw new UnsupportedOperationException();
    }

    @JSGetter
    public int emptySlotsCount() {
        return 0;
    }

    @JSGetter
    public int size() {
        return 0;
    }

    @JSFunction
    public void addItem(JsItemStack itemStack) {

    }

    @JSFunction
    public JsItemStack getItem(int slot) {
        return null;
    }

    @JSFunction
    public void setItem(int slot, JsItemStack itemStack) {

    }

    @JSFunction
    public boolean swapItems(int slot, int otherSlot, JsContainer otherContainer) {
        return false;
    }

    @JSFunction
    public boolean transferItem(int fromSlot, int toSlot, JsContainer toContainer) {
        return false;
    }
}