package com.ddf.fakeplayer.actor;

public class ActorClassTree {
    public static ActorType getEntityTypeIdLegacy(ActorType entityId) {
        return entityId;
    }

    public static boolean hasCategory(final ActorCategory category, ActorCategory testFor) {
        return (category.getValue() & testFor.getValue()) == testFor.getValue();
    }

    public static boolean isHangingEntity(final Actor inEntity) {
        return ActorClassTree.isOfType(inEntity.getEntityTypeId(), ActorType.Painting);
    }

    public static boolean isInstanceOf(final Actor inEntity, ActorType type) {
        return ActorClassTree.isTypeInstanceOf(inEntity.getEntityTypeId(), type);
    }

    public static boolean isMob(ActorType type) {
        return ActorClassTree.isTypeInstanceOf(type, ActorType.Mob);
    }

    public static boolean isMobLegacy(ActorType type) {
        if (!ActorClassTree.isMob(type)) {
            if (type.getValue() >= 10)
                return type.getValue() < ActorType.ItemEntity.getValue();
            return false;
        }
        return true;
    }

    public static boolean isOfType(ActorType type, ActorType contains) {
        return type.getValue() == contains.getValue();
    }

    public static boolean isTypeInstanceOf(ActorType type, ActorType testFor) {
        if ((testFor.getValue() & 0xFFFFFF00) == 0 || testFor.getValue() != 0)
            return type.getValue() == testFor.getValue();
        else
            return false;
    }
}
