package com.ddf.fakeplayer.actor;

import com.ddf.fakeplayer.actor.player.AbilitiesIndex;

public enum ActorCategory {
    None_21(0x0),
    Player_1(0x1),
    Mob_0(0x2),
    Monster_0(0x4),
    Humandoid(0x8),
    Animal_0(0x10),
    WaterSpawning(0x20),
    Pathable(0x40),
    Tamable(0x80),
    Ridable(0x100),
    Item_0(0x400),
    Ambient_1(0x800),
    Villager_0(0x1000),
    Arthropod_0(0x2000),
    Undead(0x4000),
    Zombie_0(0x8000),
    Minecart_0(0x10000),
    Boat(0x20000),
    NonTargetable(0x40000),
    BoatRideable_0(0x20100),
    MinecartRidable(0x10100),
    HumanoidMonster(0xC),
    WaterAnimal_0(0x30),
    TamableAnimal_0(0x90),
    UndeadMob_0(0x4004),
    ZombieMonster_0(0x8004),
    EvocationIllagerMonster(0x100C);

    private final int value;

    ActorCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ActorCategory getByValue(int value) {
        for (ActorCategory actorCategory : values()) {
            if (actorCategory.getValue() == value) {
                return actorCategory;
            }
        }
        return None_21;
    }
}
