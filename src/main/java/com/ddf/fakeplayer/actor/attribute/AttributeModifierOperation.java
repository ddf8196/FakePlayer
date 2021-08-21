package com.ddf.fakeplayer.actor.attribute;

import com.ddf.fakeplayer.actor.player.AbilitiesIndex;

public enum AttributeModifierOperation {
    OPERATION_ADDITION(0x0),
    OPERATION_MULTIPLY_BASE(0x1),
    OPERATION_MULTIPLY_TOTAL(0x2),
    OPERATION_CAP(0x3),
    TOTAL_OPERATIONS(0x4),
    OPERATION_INVALID(0x4);

    private final int value;

    AttributeModifierOperation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AttributeModifierOperation getByValue(int value) {
        for (AttributeModifierOperation attributeModifierOperation : values()) {
            if (attributeModifierOperation.getValue() == value) {
                return attributeModifierOperation;
            }
        }
        return null;
    }
}
