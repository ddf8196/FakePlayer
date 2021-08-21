package com.ddf.fakeplayer.container;

public enum ContainerType {
    NONE_2(0xF7),
    INVENTORY(0xFF),
    CONTAINER(0x0),
    WORKBENCH(0x1),
    FURNACE(0x2),
    ENCHANTMENT(0x3),
    BREWING_STAND(0x4),
    ANVIL(0x5),
    DISPENSER(0x6),
    DROPPER(0x7),
    HOPPER(0x8),
    CAULDRON(0x9),
    MINECART_CHEST(0xA),
    MINECART_HOPPER(0xB),
    HORSE(0xC),
    BEACON(0xD),
    STRUCTURE_EDITOR(0xE),
    TRADE(0xF),
    COMMAND_BLOCK(0x10),
    JUKEBOX(0x11),
    ARMOR(0x12),
    HAND(0x13),
    COMPOUND_CREATOR(0x14),
    ELEMENT_CONSTRUCTOR(0x15),
    MATERIAL_REDUCER(0x16),
    LAB_TABLE(0x17),
    LOOM(0x18),
    LECTERN(0x19),
    GRINDSTONE(0x1A),
    BLAST_FURNACE(0x1B),
    SMOKER(0x1C),
    STONECUTTER(0x1D),
    CARTOGRAPHY(0x1E);

    private final int value;

    ContainerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ContainerType getByValue(int value) {
        for (ContainerType containerType : values()) {
            if (containerType.getValue() == value) {
                return containerType;
            }
        }
        return null;
    }
}
