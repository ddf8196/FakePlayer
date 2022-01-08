package com.ddf.fakeplayer.container;

public enum ContainerID {
    CONTAINER_ID_NONE((byte) 0xFF),
    CONTAINER_ID_INVENTORY((byte) 0x0),
    CONTAINER_ID_FIRST((byte) 0x1),
    CONTAINER_ID_LAST((byte) 0x64),
    CONTAINER_ID_OFFHAND((byte) 0x77),
    CONTAINER_ID_ARMOR((byte) 0x78),
    CONTAINER_ID_CREATIVE((byte) 0x79),
    CONTAINER_ID_SELECTION_SLOTS((byte) 0x7A),
    CONTAINER_ID_PLAYER_ONLY_UI((byte) 0x7C),
    CONTAINER_ID_UNKNOWN_0xFB((byte) 0xFB), //-5
    CONTAINER_ID_UNKNOWN_0xFC((byte) 0xFC); //-4

    private final byte value;

    ContainerID(byte value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ContainerID getByValue(int value) {
        for (ContainerID containerID : values()) {
            if (containerID.getValue() == value) {
                return containerID;
            }
        }
        return ContainerID.CONTAINER_ID_NONE;
    }
}
