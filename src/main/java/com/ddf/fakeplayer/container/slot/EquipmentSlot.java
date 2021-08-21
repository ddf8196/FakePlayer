package com.ddf.fakeplayer.container.slot;

import com.ddf.fakeplayer.container.ContainerType;

public enum EquipmentSlot {
    _none(0xFFFFFFFF),
    _begin(0x0),
    _handSlot(0x0),
    Mainhand_0(0x0),
    Offhand_0(0x1),
    _armorSlot(0x2),
    Head_2(0x2),
    Torso_0(0x3),
    Legs_1(0x4),
    Feet_2(0x5),
    _containerSlot(0x6),
    Hotbar(0x6),
    Inventory(0x7),
    EnderChest_0(0x8),
    Saddle_0(0x9),
    EntityArmor(0xA),
    Chest_0(0xB),
    _count_19(0xC);

    private final int value;

    EquipmentSlot(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isHandSlot() {
        return this.getValue() < _armorSlot.getValue();
    }

    public boolean isArmorSlot() {
        if (this.getValue() >= _armorSlot.getValue())
            return this.getValue() < _containerSlot.getValue();
        return false;
    }

    public int toSlot() {
        if (this.getValue() < _armorSlot.getValue())
            return this.getValue();
        if (this.getValue() >= _containerSlot.getValue())
            return this.getValue() - 6;
        return this.getValue() - 2;
    }

    public static EquipmentSlot getByValue(int value) {
        for (EquipmentSlot equipmentSlot : values()) {
            if (equipmentSlot.getValue() == value) {
                return equipmentSlot;
            }
        }
        return null;
    }
}
