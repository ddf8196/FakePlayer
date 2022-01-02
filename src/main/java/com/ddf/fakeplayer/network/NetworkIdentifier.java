package com.ddf.fakeplayer.network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;

public final class NetworkIdentifier {
    long mGuid;
    InetSocketAddress mSock;
    NetworkIdentifier.Type mType;

    public NetworkIdentifier(long guid) {
        this.mGuid = 0xFFFFFFFFFFFFFFFFL;
        this._init(guid);
    }

    public NetworkIdentifier(InetSocketAddress address) {
        this.mGuid = 0xFFFFFFFFFFFFFFFFL;
        this._init(address);
    }

    public NetworkIdentifier(final String address, int port) {
        this.mGuid = 0xFFFFFFFFFFFFFFFFL;
        try {
            InetSocketAddress sock = new InetSocketAddress(address, port);
            this._init(sock);
        } catch (Exception e) {
            this.mType = Type.Generic;
        }
    }

    public NetworkIdentifier() {
        this.mGuid = 0xFFFFFFFFFFFFFFFFL;
        this.mSock = null;
        this.mType = Type.Generic;
    }

    private void _init(final long guid) {
        this.mGuid = guid;
        this.mType = Type.RakNet;
    }

    private void _init(InetSocketAddress sock) {
        this.mSock = sock;
        if (sock.getAddress() instanceof Inet4Address) {
            this.mType = Type.Address;
        } else if (sock.getAddress() instanceof Inet6Address) {
            this.mType = Type.Address6;
        } else {
            this.mType = Type.Generic;
        }
    }

    public final String getAddress() {
        if (this.isType(Type.Address) || this.isType(Type.Address6)) {
            return this.getSocketAddress().getAddress().getHostAddress();
        } else {
            return "";
        }
    }

    public final InetSocketAddress getSocketAddress() {
        return this.mSock;
    }

    public final boolean isType(NetworkIdentifier.Type type) {
        return this.mType == type;
    }

    public final boolean equalsTypeData(final NetworkIdentifier other) {
        switch (this.mType) {
            case RakNet:
                return this.mGuid == other.mGuid;
            case Address:
            case Address6:
                return getSocketAddress().equals(other.getSocketAddress());
            case Generic:
                return true;
            default:
                return false;
        }
    }

    public final boolean equals(final NetworkIdentifier other) {
        if (this.isType(other.mType) )
            return this.equalsTypeData(other);
        return false;
    }

    public enum Type {
        RakNet,
        Address,
        Address6,
        Generic,
    }
}
