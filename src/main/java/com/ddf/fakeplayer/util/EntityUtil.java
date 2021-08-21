package com.ddf.fakeplayer.util;

import com.ddf.fakeplayer.actor.*;
import com.ddf.fakeplayer.util.tuple.Tuple2;

import java.util.Map;

public class EntityUtil {
    public static Tuple2<String, String> EntityTypeToStringAndNamespace(ActorType entityType, String nameOut, String namespaceOut) {
        ActorMapping actorMapping = ActorMapping.ENTITY_TYPE_MAP.get(entityType);
        if (entityType == ActorType.Undefined_2 || actorMapping == null) {
            nameOut = "unknown";
            namespaceOut = "";
        } else {
            nameOut = actorMapping.mPrimaryName;
            namespaceOut = actorMapping.mNamespace;
        }
        return new Tuple2<>(nameOut, namespaceOut);
    }


    public static String EntityCanonicalName(ActorType entityType) {
        if (entityType != ActorType.Undefined_2) {
            for (Map.Entry<ActorType, ActorMapping> mapping : ActorMapping.ENTITY_TYPE_MAP.entrySet()) {
                if (ActorClassTree.isOfType(mapping.getKey(), entityType))
                    return mapping.getValue().getCanonicalName();
            }
        }
        return "minecraft:unknown";
    }

    public static String EntityTypeIdWithoutCategories(String retstr, ActorType entityType, ActorTypeNamespaceRules namespaceRule) {
        if (entityType != ActorType.Undefined_2) {
            for (Map.Entry<ActorType, ActorMapping> mapping : ActorMapping.ENTITY_TYPE_MAP.entrySet()) {
                if (ActorClassTree.isOfType(mapping.getKey(), entityType)) {
                    retstr = mapping.getValue().getMappingName(namespaceRule);
                    return retstr;
                }
            }
        }
        retstr = "unknown";
        return retstr;
    }

    @NotImplemented
    public static void _defineEntityData(SynchedActorData data) {
    }
}
