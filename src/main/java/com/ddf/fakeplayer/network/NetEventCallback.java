package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.actor.player.Player;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;

public interface NetEventCallback extends BedrockPacketHandler {
    default void onPlayerReady(Player player) { //+0
    }

    default void onConnect(final NetworkIdentifier hostId) { //+3
    }

    default void onUnableToConnect() { //+4
    }

    default void onTick() { //+5
    }

    default void onStoreOfferReceive(final boolean showAllOffers, final String offerID) { //+6
    }

    default void onDisconnect(final NetworkIdentifier id, final String message, boolean skipMessage, final String telemetryOverride) { //+7
    }

    boolean allowIncomingPacketId(final NetworkIdentifier id, int packetId); //+8

    default void onWebsocketRequest(final String serverAddress, final String payload) { //+9
    }

    default void onTransferRequest(final NetworkIdentifier source, final String serverAddress, int serverPort) { //+10
    }
}
