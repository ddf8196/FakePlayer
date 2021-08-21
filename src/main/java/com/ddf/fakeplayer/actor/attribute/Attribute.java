package com.ddf.fakeplayer.actor.attribute;

public class Attribute {
    private RedefinitionMode mRedefinitionMode;
    private boolean mSyncable;
    private /*uint32_t*/int mIDValue;
    private String mName;

    public Attribute(final String name, RedefinitionMode redefMode, boolean isSyncable) {
        this.mRedefinitionMode = redefMode;
        this.mSyncable = isSyncable;
        this.mName = name;
        this.mIDValue = AttributeCollection.instance().addAttribute(name, this);
    }

    public static Attribute getByName(final String attribute) {
        return AttributeCollection.instance().getAttribute(attribute);
    }

    public /*uint32_t*/int getIDValue() {
        return this.mIDValue;
    }

    public String getName() {
        return this.mName;
    }

    public RedefinitionMode getRedefinitionMode() {
        return this.mRedefinitionMode;
    }

    public boolean isClientSyncable() {
        return this.mSyncable;
    }
}