package com.ddf.fakeplayer.container.slot;

public enum ArmorSlot {
    Head,
    Torso,
    Legs,
    Feet,
    _count_1;

    public static ArmorSlot toArmorSlot(int index) {
        return values()[index];
    }
}
