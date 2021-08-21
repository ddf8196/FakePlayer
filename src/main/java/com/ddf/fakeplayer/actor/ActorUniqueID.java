package com.ddf.fakeplayer.actor;

import java.util.UUID;

public class ActorUniqueID {
    public static final long INVALID_ID = -1;

    public static long fromUUID(UUID uuid) {
        return uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits();
    }

    public static boolean isValid(long actorUniqueID) {
        return actorUniqueID != INVALID_ID;
    }

    public static boolean toBoolean(long actorUniqueID) {
        return isValid(actorUniqueID);
    }
}
