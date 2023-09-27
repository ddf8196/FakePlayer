package com.ddf.fakeplayer.network;


import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public interface PacketObserver {
    void packetSentTo(final NetworkIdentifier target, final BedrockPacket BedrockPacket, int size); //+2
    void packetReceivedFrom(final NetworkIdentifier source, final BedrockPacket BedrockPacket, int size); //+3
    void dataSentTo(final NetworkIdentifier target, byte[] data); //+4
    void dataReceivedFrom(final NetworkIdentifier source, final byte[] data); //+5
}
