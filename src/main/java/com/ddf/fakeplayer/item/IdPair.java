package com.ddf.fakeplayer.item;

import java.util.HashMap;
import java.util.Objects;

public class IdPair {
    public short id;
    public short aux;

    public IdPair(short i, short a) {
        this.id = i;
        this.aux = a;
    }

    static HashMap<IdPair, IdPair> PROMOTE_MAP = new HashMap<>();

    static {
        PROMOTE_MAP.put(new IdPair((short)349, (short)1), new IdPair((short)460, (short)0));
        PROMOTE_MAP.put(new IdPair((short)349, (short)2), new IdPair((short)461, (short)0));
        PROMOTE_MAP.put(new IdPair((short)349, (short)3), new IdPair((short)462, (short)0));
        PROMOTE_MAP.put(new IdPair((short)350, (short)1), new IdPair((short)463, (short)0));
        PROMOTE_MAP.put(new IdPair((short)322, (short)1), new IdPair((short)466, (short)0));
    }

    public static void PromoteItemIdPair(IdPair inout) {
        if (PROMOTE_MAP.containsKey(inout)) {
            IdPair idPair = PROMOTE_MAP.get(inout);
            inout.id = idPair.id;
            inout.aux = idPair.aux;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdPair idPair = (IdPair) o;
        return id == idPair.id && aux == idPair.aux;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, aux);
    }
}
