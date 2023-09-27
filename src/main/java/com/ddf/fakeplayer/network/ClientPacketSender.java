package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.client.Client;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.util.ArrayList;
import java.util.function.Function;

public class ClientPacketSender extends PacketSender {
    private final Client client;

    public ClientPacketSender(Client client) {
        super((byte) 0);
        this.client = client;
    }

    @Override
    public void send(BedrockPacket packet) {
        sendToServer(packet);
    }

    @Override
    public void sendToServer(BedrockPacket packet) {
        client.sendPacket(packet);
    }

    @Override
    public void sendToClient(NetworkIdentifier id, BedrockPacket packet, byte subid) {
    }

    @Override
    public void sendToClients(ArrayList<NetworkIdentifierWithSubId> ids, BedrockPacket packet) {
    }

    @Override
    public void sendBroadcast(BedrockPacket packet) {
    }

    @Override
    public void sendBroadcast(NetworkIdentifier exceptId, byte exceptSubid, BedrockPacket packet) {
    }

    @Override
    public void flush(NetworkIdentifier id, Function<Void, Void> callback) {
    }
}
