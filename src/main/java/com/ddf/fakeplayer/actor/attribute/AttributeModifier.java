package com.ddf.fakeplayer.actor.attribute;

import java.util.UUID;

public class AttributeModifier {
    private float mAmount;
    private int mOperation;
    private int mOperand;
    private String mName;
    private UUID mId;
    private boolean mSerialize;

    public float getAmount() {
        return this.mAmount;
    }

    public int getOperation() {
        return this.mOperation;
    }
}
