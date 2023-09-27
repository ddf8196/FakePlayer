package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.util.NotImplemented;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.util.ArrayList;
import java.util.function.Function;

public class LoopbackPacketSender extends PacketSender {
    private NetworkHandler mNetwork;
    private ArrayList<NetEventCallback> mLoopbackCallbacks;
    private ArrayList<Player> mUserList;
    private ArrayList<NetworkIdentifierWithSubId> mTempUserIds;

    public LoopbackPacketSender(/*uint8_t SubClientId*/byte subid, NetworkHandler network) {
        super(subid);
        this.mNetwork = network;
        this.mLoopbackCallbacks = new ArrayList<>();
        this.mUserList = null;
        this.mTempUserIds = new ArrayList<>();
    }

    @Override
    public void send(BedrockPacket packet) {
//        if (this.mNetwork.isServer())
//            this.sendBroadcast(packet);
//        else
//            this.sendToServer(packet);
    }

    @Override
    public void sendToServer(BedrockPacket packet) {
//        packet.setClientSubId(this.mSenderSubId);
//        this.mNetwork.send(this.mNetwork.getServerId(), packet, (byte) 0);
    }

    @NotImplemented
    @Override
    public void sendToClient(NetworkIdentifier id, BedrockPacket packet, byte subid) {
    }

    @NotImplemented
    @Override
    public void sendToClients(ArrayList<NetworkIdentifierWithSubId> ids, BedrockPacket packet) {
    }

    @NotImplemented
    @Override
    public void sendBroadcast(BedrockPacket packet) {
    }

    @NotImplemented
    @Override
    public void sendBroadcast(NetworkIdentifier exceptId, byte exceptSubid, BedrockPacket packet) {
    }

    @Override
    public void flush(NetworkIdentifier id, Function<Void, Void> callback) {
        this.mNetwork.flush(id, callback);
    }

    public final void addLoopbackCallback(NetEventCallback callback) {
        this.mLoopbackCallbacks.add(callback);
    }

    public final void removeLoopbackCallback(NetEventCallback callback) {
        this.mLoopbackCallbacks.remove(callback);
    }

    public final void setUserList(final ArrayList<Player> userList) {
        this.mUserList = userList;
    }
}
