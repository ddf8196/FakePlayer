package com.ddf.fakeplayer.actor.attribute;

import java.util.HashMap;

public class AttributeCollection {
    private static final AttributeCollection INSTANCE = new AttributeCollection();
    private final HashMap<String, Attribute> mAttributesMap = new HashMap<>();
    private /*uint32_t*/int mIDValueIndex = 0;

    private AttributeCollection() {}

    public static AttributeCollection instance() {
        return INSTANCE;
    }

    public /*uint32_t*/int addAttribute(final String name, Attribute attribute) {
        this.mAttributesMap.put(name, attribute);
        return ++this.mIDValueIndex;
    }

    public Attribute getAttribute(final String name) {
        if (this.mAttributesMap.containsKey(name)) {
            return this.mAttributesMap.get(name);
        }
//        if (ServiceLocator<ContentLog>.isSet()) {
//            ContentLog contentLog = ServiceLocator<ContentLog>.get();
//            if (contentLog.isEnabled()) {
//                contentLog.log(Error, Actor, "Cannot find attribute %s", name);
//            }
//        }
        return this.mAttributesMap.entrySet().iterator().next().getValue();
    }

    public static boolean hasAttribute(final String name) {
        return AttributeCollection.instance().mAttributesMap.containsKey(name);
    }
}
