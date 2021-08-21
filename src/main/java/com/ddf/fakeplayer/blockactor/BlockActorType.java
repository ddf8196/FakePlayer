package com.ddf.fakeplayer.blockactor;

public enum BlockActorType {
    Undefined_6(0x0),
    Furnace(0x1),
    Chest(0x2),
    NetherReactor(0x3),
    Sign(0x4),
    MobSpawner(0x5),
    Skull(0x6),
    FlowerPot(0x7),
    BrewingStand(0x8),
    EnchantingTable_0(0x9),
    DaylightDetector(0xA),
    Music(0xB),
    Comparator(0xC),
    Dispenser(0xD),
    Dropper(0xE),
    Hopper(0xF),
    Cauldron(0x10),
    ItemFrame(0x11),
    PistonArm(0x12),
    MovingBlock_0(0x13),
    Chalkboard_0(0x14),
    Beacon(0x15),
    EndPortal(0x16),
    EnderChest(0x17),
    EndGateway(0x18),
    ShulkerBox(0x19),
    CommandBlock(0x1A),
    Bed_0(0x1B),
    Banner(0x1C),
    StructureBlock(0x20),
    Jukebox(0x21),
    ChemistryTable(0x22),
    Conduit_0(0x23),
    JigsawBlock(0x24),
    Lectern_0(0x25),
    BlastFurnace(0x26),
    Smoker(0x27),
    Bell_0(0x28),
    Campfire(0x29),
    BarrelBlock(0x2A),
    Beehive(0x2B),
    _count_14(0x2C);

    private final int value;

    BlockActorType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BlockActorType getByValue(int value) {
        for (BlockActorType blockActorType : values()) {
            if (blockActorType.getValue() == value) {
                return blockActorType;
            }
        }
        return Undefined_6;
    }
}
