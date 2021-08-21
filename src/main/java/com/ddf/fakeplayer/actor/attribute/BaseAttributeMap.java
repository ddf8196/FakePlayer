package com.ddf.fakeplayer.actor.attribute;

import com.ddf.fakeplayer.actor.attribute.AttributeInstance;
import com.ddf.fakeplayer.actor.attribute.AttributeInstanceHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseAttributeMap implements Iterable<Map.Entry<Integer, AttributeInstance>> {
    public static AttributeInstance mInvalidInstance = new AttributeInstance();
    private HashMap</*unsigned int*/Integer, AttributeInstance> mInstanceMap = new HashMap<>();
    private ArrayList<AttributeInstanceHandle> mDirtyAttributes = new ArrayList<>();

    public void clearDirtyAttributes() {
        this.mDirtyAttributes.clear();
    }

    public final ArrayList<AttributeInstanceHandle> getDirtyAttributes() {
        return this.mDirtyAttributes;
    }

    public final AttributeInstance getInstance(final Attribute attribute) {
        return this.getInstance(attribute.getIDValue());
    }

    public final AttributeInstance getInstance(final String name) {
        return this.getInstance(Attribute.getByName(name));
    }

    public final AttributeInstance getInstance(/*uint32_t*/int idValue) {
        if (this.mInstanceMap.containsKey(idValue)) {
            return this.mInstanceMap.get(idValue);
        } else {
            return BaseAttributeMap.mInvalidInstance;
        }
    }

    public AttributeInstance getMutableInstance(final Attribute attribute) {
        return this.getMutableInstance(attribute.getIDValue());
    }

    public AttributeInstance getMutableInstance(final String name) {
        return this.getMutableInstance(Attribute.getByName(name));
    }

    public AttributeInstance getMutableInstance(/*uint32_t*/int idValue) {
        return this.mInstanceMap.getOrDefault(idValue, null);
    }

    public ArrayList<AttributeInstanceHandle> getSyncableAttributes() {
        ArrayList<AttributeInstanceHandle> retstr = new ArrayList<>();
        for (Map.Entry<Integer, AttributeInstance> instance : this.mInstanceMap.entrySet()) {
            if (instance.getValue().getAttribute().isClientSyncable()) {
                retstr.add(instance.getValue().getHandle());
            }
        }
        return retstr;
    }

    public void onAttributeModified(final AttributeInstance attributeInstance) {
        if (attributeInstance.getAttribute().isClientSyncable()) {
            this.mDirtyAttributes.add(attributeInstance.getHandle());
        }
    }

    public AttributeInstance registerAttribute(final Attribute baseAttribute) {
        AttributeInstance attribute = this.getMutableInstance(baseAttribute);
        if (attribute != null)
            return attribute;
        this.mInstanceMap.put(baseAttribute.getIDValue(), new AttributeInstance(this, baseAttribute));
        return this.mInstanceMap.get(baseAttribute.getIDValue());
    }

    @Override
    public Iterator<Map.Entry<Integer, AttributeInstance>> iterator() {
        return this.mInstanceMap.entrySet().iterator();
    }
}
