package com.ddf.fakeplayer.item;

public enum CooldownType {
    TypeNone(0xFFFFFFFF),
    ChorusFruit_0(0x0),
    EnderPearl(0x1),
    IceBomb_1(0x2),
    Count_24(0x3);

    private final int value;

    CooldownType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CooldownType getByValue(int value) {
        for (CooldownType cooldownType : values()) {
            if (cooldownType.getValue() == value) {
                return cooldownType;
            }
        }
        return null;
    }
}
