package com.ddf.fakeplayer.actor.attribute;

public class SharedAttributes {
    public static final Attribute HEALTH = new Attribute("minecraft:health", RedefinitionMode.KeepCurrent, true);
    public static final Attribute FOLLOW_RANGE = new Attribute("minecraft:follow_range", RedefinitionMode.UpdateToNewDefault, false);
    public static final Attribute KNOCKBACK_RESISTANCE = new Attribute("minecraft:knockback_resistance", RedefinitionMode.UpdateToNewDefault, false);
    public static final Attribute MOVEMENT_SPEED = new Attribute("minecraft:movement", RedefinitionMode.UpdateToNewDefault, true);
    public static final Attribute UNDERWATER_MOVEMENT_SPEED = new Attribute("minecraft:underwater_movement", RedefinitionMode.UpdateToNewDefault, true);
    public static final Attribute ATTACK_DAMAGE = new Attribute("minecraft:attack_damage", RedefinitionMode.UpdateToNewDefault, false);
    public static final Attribute ABSORPTION = new Attribute("minecraft:absorption", RedefinitionMode.UpdateToNewDefault, true);
    public static final Attribute LUCK = new Attribute("minecraft:luck", RedefinitionMode.UpdateToNewDefault, true);
    public static final Attribute JUMP_STRENGTH = new  Attribute("minecraft:horse.jump_strength", RedefinitionMode.UpdateToNewDefault, true);

    public static final String LAVA_MOVEMENT = "minecraft:lava_movement";

    public static void init() {
    }
}
