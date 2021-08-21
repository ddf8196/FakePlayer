package com.ddf.fakeplayer.actor;

import com.ddf.fakeplayer.actor.attribute.AttributeOperands;

public enum ActorFlags {
    Unknown_25(0xFFFFFFFF),
    ONFIRE(0x0),
    SNEAKING(0x1),
    RIDING(0x2),
    SPRINTING(0x3),
    USINGITEM(0x4),
    INVISIBLE(0x5),
    TEMPTED(0x6),
    INLOVE(0x7),
    SADDLED(0x8),
    POWERED(0x9),
    IGNITED(0xA),
    BABY(0xB),
    CONVERTING(0xC),
    CRITICAL(0xD),
    CAN_SHOW_NAME(0xE),
    ALWAYS_SHOW_NAME(0xF),
    NOAI(0x10),
    SILENT(0x11),
    WALLCLIMBING(0x12),
    CANCLIMB(0x13),
    CANSWIM(0x14),
    CANFLY(0x15),
    CANWALK(0x16),
    RESTING(0x17),
    SITTING(0x18),
    ANGRY(0x19),
    INTERESTED(0x1A),
    CHARGED(0x1B),
    TAMED(0x1C),
    ORPHANED(0x1D),
    LEASHED(0x1E),
    SHEARED(0x1F),
    GLIDING(0x20),
    ELDER(0x21),
    MOVING(0x22),
    BREATHING(0x23),
    CHESTED(0x24),
    STACKABLE(0x25),
    SHOW_BOTTOM(0x26),
    STANDING(0x27),
    SHAKING(0x28),
    IDLING(0x29),
    CASTING(0x2A),
    CHARGING(0x2B),
    WASD_CONTROLLED(0x2C),
    CAN_POWER_JUMP(0x2D),
    LINGERING(0x2E),
    HAS_COLLISION(0x2F),
    HAS_GRAVITY(0x30),
    FIRE_IMMUNE(0x31),
    DANCING(0x32),
    ENCHANTED(0x33),
    RETURNTRIDENT(0x34),
    CONTAINER_IS_PRIVATE(0x35),
    IS_TRANSFORMING(0x36),
    DAMAGENEARBYMOBS(0x37),
    SWIMMING(0x38),
    BRIBED(0x39),
    IS_PREGNANT(0x3A),
    LAYING_EGG(0x3B),
    RIDER_CAN_PICK(0x3C),
    TRANSITION_SITTING(0x3D),
    EATING(0x3E),
    LAYING_DOWN(0x3F),
    SNEEZING(0x40),
    TRUSTING(0x41),
    ROLLING(0x42),
    SCARED(0x43),
    IN_SCAFFOLDING(0x44),
    OVER_SCAFFOLDING(0x45),
    FALL_THROUGH_SCAFFOLDING(0x46),
    BLOCKING(0x47),
    TRANSITION_BLOCKING(0x48),
    BLOCKED_USING_SHIELD(0x49),
    BLOCKED_USING_DAMAGED_SHIELD(0x4A),
    SLEEPING(0x4B),
    WANTS_TO_WAKE(0x4C),
    TRADE_INTEREST(0x4D),
    DOOR_BREAKER(0x4E),
    BREAKING_OBSTRUCTION(0x4F),
    DOOR_OPENER(0x50),
    IS_ILLAGER_CAPTAIN(0x51),
    STUNNED(0x52),
    ROARING(0x53),
    DELAYED_ATTACK(0x54),
    IS_AVOIDING_MOBS(0x55),
    FACING_TARGET_TO_RANGE_ATTACK(0x56),
    HIDDEN_WHEN_INVISIBLE(0x57),
    IS_IN_UI(0x58),
    STALKING(0x59),
    EMOTING(0x5A),
    CELEBRATING(0x5B),
    Count_7(0x5C);

    private final int value;

    ActorFlags(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ActorFlags getByValue(int value) {
        for (ActorFlags actorFlags : values()) {
            if (actorFlags.getValue() == value) {
                return actorFlags;
            }
        }
        return null;
    }
}
