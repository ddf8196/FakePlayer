package com.ddf.fakeplayer.block;

public enum BlockProperty {
    None_43(0x0),
    Stair(0x1),
    HalfSlab(0x2),
    Hopper_0(0x4),
    TopSnow_0(0x8),
    FenceGate(0x10),
    Leaf(0x20),
    ThinConnects2D(0x40),
    Connects2D(0x80),
    Carpet_0(0x100),
    Button_0(0x200),
    Door(0x400),
    Portal_3(0x800),
    Heavy(0x1000),
    Snow_1(0x2000),
    Trap(0x4000),
    Sign_0(0x8000),
    Walkable(0x10000),
    PressurePlate(0x20000),
    PistonBlockGrabber(0x40000),
    TopSolidBlocking(0x80000),
    SolidBlocking(0x100000),
    CubeShaped(0x200000),
    Power_NO(0x400000),
    Power_BlockDown(0x800000),
    Immovable(0x1000000),
    BreakOnPush(0x2000000),
    Piston_1(0x4000000),
    InfiniBurn(0x8000000),
    RequiresWorldBuilder(0x10000000),
    CausesDamage(0x20000000),
    BreaksWhenFallenOnByHeavy(0x40000000),
    OnlyPistonPush(0x80000000),
    Liquid_0(0x100000000L),
    CanBeBuiltOver(0x200000000L),
    SnowRecoverable(0x400000000L),
    Scaffolding(0x800000000L),
    CanSupportCenterHangingBlock(0x1000000000L),
    BreaksWhenHitByArrow(0x2000000000L),
    Unwalkable(0x4000000000L),
    Impenetrable(0x8000000000L),
    Hollow(0x10000000000L),
    OperatorBlock(0x20000000000L),
    SupportedByFlowerPot(0x40000000000L),
    PreventsJumping(0x80000000000L),
    ContainsHoney(0x100000000000L),
    Slime_2(0x200000000000L);

    private final long value;

    BlockProperty(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public static BlockProperty getByValue(long value) {
        for (BlockProperty blockProperty : values()) {
            if (blockProperty.getValue() == value) {
                return blockProperty;
            }
        }
        return None_43;
    }
}
