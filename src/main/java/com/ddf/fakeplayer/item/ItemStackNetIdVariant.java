package com.ddf.fakeplayer.item;

public final class ItemStackNetIdVariant {
    public static final int SERVER_NET_ID = 0;
    public static final int CLIENT_REQUEST_ID = 1;
    public static final int CLIENT_LEGACY_REQUEST_ID = 2;

    private int id = 0;
    private int type = SERVER_NET_ID;

    public ItemStackNetIdVariant() {}

    public ItemStackNetIdVariant(int id, int type) {
        this.id = id;
    }

    public final boolean hasAnyValidId() {
        return this.hasServerNetId()
                || this.hasClientRequestId()
                || this.hasLegacyClientRequestId();
    }

    public final boolean hasServerNetId() {
        return this.type == SERVER_NET_ID && this.id > 0;
    }

    public final boolean hasClientRequestId() {
        return this.type == CLIENT_REQUEST_ID && this.id < 0 && (this.id & 1) != 0;
    }

    public final boolean hasLegacyClientRequestId() {
        return this.type == CLIENT_LEGACY_REQUEST_ID && this.id < -1 && (this.id & 1) == 0;
    }

    public final boolean isServerNetId() {
        return this.type == SERVER_NET_ID;
    }

    public final boolean isClientRequestId() {
        return this.type == CLIENT_REQUEST_ID;
    }

    public final boolean isLegacyClientRequestId() {
        return this.type == CLIENT_LEGACY_REQUEST_ID;
    }

    public final void set(ItemStackNetIdVariant variant) {
        set(variant.id, variant.type);
    }

    public final void set(int id, int type) {
        this.id = id;
        this.type = type;
    }

    public final int tryGetServerNetId() {
        if (this.type == SERVER_NET_ID)
            return this.id;
        return 0;
    }

    public final int tryGetRequestId() {
        if (this.type == CLIENT_REQUEST_ID)
            return this.id;
        return 0;
    }

    public final int tryGetLegacyRequestId() {
        if (this.type == CLIENT_LEGACY_REQUEST_ID)
            return this.id;
        return 0;
    }

    public final int toInt() {
        return id;
    }

    @Override
    public String toString() {
        int id;
        if ((id = this.tryGetServerNetId()) != 0) {
            return "[ItemStackNetId: " + id + "]";
        } else if ((id = this.tryGetRequestId()) != 0) {
            return "[ItemStackRequestId: " + id + "]";
        } else if ((id = this.tryGetLegacyRequestId()) != 0) {
            return "[ItemStackLegacyRequestId: " + id + "]";
        }
        return "[Invalid item stack net ID variant]";
    }
}
