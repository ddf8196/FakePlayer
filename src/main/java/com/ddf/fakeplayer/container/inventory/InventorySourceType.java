package com.ddf.fakeplayer.container.inventory;

public enum InventorySourceType {
    InvalidInventory(0xFFFFFFFF),
    ContainerInventory(0x0),
    GlobalInventory(0x1),
    WorldInteraction(0x2),
    CreativeInventory(0x3),
    UntrackedInteractionUI(0x64),
    NonImplementedFeatureTODO(0x1869F);

    private final int value;

    InventorySourceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static InventorySourceType getByValue(int value) {
        for (InventorySourceType inventorySourceType : values()) {
            if (inventorySourceType.getValue() == value) {
                return inventorySourceType;
            }
        }
        return null;
    }
}
