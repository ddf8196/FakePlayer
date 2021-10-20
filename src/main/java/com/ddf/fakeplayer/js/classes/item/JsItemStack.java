package com.ddf.fakeplayer.js.classes.item;

import com.ddf.fakeplayer.js.classes.BaseScriptableObject;
import com.ddf.fakeplayer.js.util.ConstructableInJS;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;

@ConstructableInJS
public class JsItemStack extends BaseScriptableObject {
    private JsItemType itemType;
    private int amount;
    private int data;

    public JsItemStack() {}

    @JSConstructor
    public JsItemStack(JsItemType itemType, int amount, int data) {
    }

    public JsItemType getItemType() {
        return itemType;
    }

    public void setItemType(JsItemType itemType) {
        this.itemType = itemType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

//----------------------------------------------------------------------------------------------------------------------

    @JSGetter
    public int amount() {
        return amount;
    }

    @JSGetter
    public int data() {
        return data;
    }

    @JSGetter
    public String id() {
        return itemType.getName();
    }
}
