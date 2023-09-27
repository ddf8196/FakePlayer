package com.ddf.fakeplayer.network;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class PacketSender {
    protected  /*uint8_t SubClientId*/byte mSenderSubId;

    protected PacketSender(/*uint8_t SubClientId*/byte subid) {
        this.mSenderSubId = subid;
    }

    public abstract void send(BedrockPacket packet);
    public abstract void sendToServer(BedrockPacket packet);
    public abstract void sendToClient(final NetworkIdentifier id, final BedrockPacket packet, /*uint8_t SubClientId*/byte subid);
    public abstract void sendToClients(final ArrayList<NetworkIdentifierWithSubId> ids, final BedrockPacket packet);
    public abstract void sendBroadcast(final BedrockPacket packet);
    public abstract void sendBroadcast(final NetworkIdentifier exceptId, /*uint8_t SubClientId*/byte exceptSubid, final BedrockPacket packet);
    public abstract void flush(final NetworkIdentifier id, Function<Void, Void> callback);
}
