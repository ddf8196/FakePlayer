package com.ddf.fakeplayer.actor.definition;

import com.ddf.fakeplayer.actor.ActorType;
import com.ddf.fakeplayer.actor.ActorTypeNamespaceRules;
import com.ddf.fakeplayer.util.EntityUtil;
import com.ddf.fakeplayer.util.NotImplemented;

public class ActorDefinitionIdentifier {
    public static final String NAMESPACE_SEPARATOR = ":";
    public static final String EVENT_BEGIN = "<";
    public static final String EVENT_END = ">";

    private String mNamespace;
    private String mIdentifier;
    private String mInitEvent;
    private String mFullName;
    private String mCanonicalName;

    public ActorDefinitionIdentifier() {
        this.mNamespace = "";
        this.mIdentifier = "";
        this.mInitEvent = "";
        this.mFullName = "";
        this.mCanonicalName = "";
    }

    public ActorDefinitionIdentifier(ActorType type) {
        this.mNamespace = "minecraft";
        this.mIdentifier = EntityUtil.EntityTypeIdWithoutCategories(this.mIdentifier, type, ActorTypeNamespaceRules.ReturnWithoutNamespace);
        this.mInitEvent = "";
        this.mFullName = "";
        this.mCanonicalName = EntityUtil.EntityCanonicalName(type);
        this._initialize();
    }

    public ActorDefinitionIdentifier(String namespace, String identifier, String initEvent) {
        this.mNamespace = namespace;
        this.mIdentifier = identifier;
        this.mInitEvent = initEvent;
        this.mFullName = "";
        this.mCanonicalName = "";
        this._initialize();
    }

    public void _initialize() {
        if (this.mCanonicalName.isEmpty()) {
            this.mCanonicalName = this.mNamespace + ActorDefinitionIdentifier.NAMESPACE_SEPARATOR + this.mIdentifier;
        }
        this.mFullName = this.mCanonicalName + ActorDefinitionIdentifier.EVENT_BEGIN + this.mInitEvent + ActorDefinitionIdentifier.EVENT_END;
    }

    @NotImplemented
    public String getCanonicalHash() {
        return null;
    }

    @NotImplemented
    public String getCanonicalName() {
        return null;
    }
}
