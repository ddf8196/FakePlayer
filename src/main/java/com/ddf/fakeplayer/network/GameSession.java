package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.level.Level;

public class GameSession {
    NetworkHandler mNetworkHandler;
    Level mLevel;
//    ServerNetworkHandler mServerNetworkHandler;
    NetEventCallback mLegacyClientNetworkHandler;
    NetEventCallback mClientNetworkHandler;
    LoopbackPacketSender mLoopbackPacketSender;
    /*uint8_t SubClientId*/byte mClientSubId;
}
