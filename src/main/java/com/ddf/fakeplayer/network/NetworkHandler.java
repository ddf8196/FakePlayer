package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.util.NotImplemented;
import com.ddf.fakeplayer.util.Scheduler;
import com.nukkitx.network.raknet.RakNet;
import com.nukkitx.network.raknet.RakNetServer;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class NetworkHandler {
    RakNet mRakNetInstance;
//    Unique<LocalConnector> mLocalConnector;
//    Unique<RakNetServerLocator> mRakNetServerLocator;
//    Unique<UPNPInterface> mUPnPInterface;
//    Bedrock::Threading::RecursiveMutex mConnectionsMutex;
    ArrayList<NetworkHandler.Connection> mConnections;
    HashMap<NetworkIdentifier, NetworkHandler.Connection> mConnectionMap;
//    size_t mCurrentConnection;
//    Bedrock::Threading::IAsyncResult<void>::Handle mReceiveTask;
//    TaskGroup mReceiveTaskGroup;
    PacketObserver mPacketObserver;
//    Scheduler mMainThread;
//    String mReceiveBuffer;
//    NetworkIdentifier mHostingPlayerId;
//    /*uint8_t SubClientId*/byte mHostingPlayerSubId;
//    String mSendBuffer;
//    BinaryStream mSendStream;
//    ResourcePackTransmissionManager mResourcePackTransmissionManager;
//    Unique<NetworkHandler.IncomingPacketQueue> mIncomingPackets[4];
//    bool mUseIPv6Only;
//    uint16_t mDefaultGamePort;
//    uint16_t mDefaultGamePortv6;
//    NetworkPacketEventCoordinator mPacketEventCoordinator;
//    NetworkStatistics mNetworkStatistics;
//    uint16_t mCompressionThreshold;

    public NetworkHandler(Scheduler receiveThread, NetworkHandler.NetworkStatisticsConfig statsConfig) {
    }

    public final boolean isServer() {
        return this.mRakNetInstance instanceof RakNetServer;
    }

    public final NetworkIdentifier getLocalNetworkId() {
        return new NetworkIdentifier(mRakNetInstance.getGuid());
    }

    public final NetworkIdentifier getPrimaryNetworkId() {
        return this.getLocalNetworkId();
    }

    public final NetworkIdentifier getServerId() {
        if (this.isServer()) {
            return this.getPrimaryNetworkId();
        } else if (this.mConnections.isEmpty()) {
            return new NetworkIdentifier(0xFFFFFFFFFFFFFFFFL);
        } else {
            return this.mConnections.get(0).mId;
        }
    }

    public final void send(final NetworkIdentifier id, final BedrockPacket packet, final  /*uint8_t SubClientId*/byte senderSubId) {
        if (this.mPacketObserver != null) {
//            mPacketObserver.packetSentTo(id, packet, data.size());
        }
    }

    @NotImplemented
    public final void flush(NetworkIdentifier id, Function<Void, Void> callback) {

    }

    public enum NetworkStatisticsConfig {
        None_23,
        Client,
        Server,
    }

    public static final class Connection {
        private NetworkIdentifier mId;
        private NetworkHandler.Connection.Type mType;
        private BedrockSession session;

        public enum Type {
            Remote_0,
            Local_1,
        }
    }

}
