package com.ddf.fakeplayer.actor.player;

public enum AbilitiesIndex {
    Invalid_3(0xFFFF),
    Build(0x0),
    Mine(0x1),
    DoorsAndSwitches(0x2),
    OpenContainers(0x3),
    AttackPlayers(0x4),
    AttackMobs(0x5),
    OperatorCommands(0x6),
    Teleport(0x7),
    Invulnerable(0x8),
    Flying(0x9),
    MayFly(0xA),
    Instabuild(0xB),
    Lightning(0xC),
    FlySpeed(0xD),
    WalkSpeed(0xE),
    Muted(0xF),
    WorldBuilder(0x10),
    NoClip(0x11),
    ExposedAbilityCount(0x8),
    AbilityCount(0x12);

    private final int value;

    AbilitiesIndex(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AbilitiesIndex getByValue(int value) {
        for (AbilitiesIndex abilitiesIndex : values()) {
            if (abilitiesIndex.getValue() == value) {
                return abilitiesIndex;
            }
        }
        return null;
    }
}
