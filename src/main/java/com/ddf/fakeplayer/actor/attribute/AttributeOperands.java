package com.ddf.fakeplayer.actor.attribute;

import com.ddf.fakeplayer.actor.ActorType;

public enum AttributeOperands {
    OPERAND_MIN(0x0),
    OPERAND_MAX(0x1),
    OPERAND_CURRENT(0x2),
    TOTAL_OPERANDS(0x3),
    OPERAND_INVALID(0x3);

    private final int value;

    AttributeOperands(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AttributeOperands getByValue(int value) {
        for (AttributeOperands attributeOperands : values()) {
            if (attributeOperands.getValue() == value) {
                return attributeOperands;
            }
        }
        return null;
    }
}
