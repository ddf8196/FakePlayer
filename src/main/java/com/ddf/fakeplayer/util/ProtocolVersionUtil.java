package com.ddf.fakeplayer.util;

import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v291.Bedrock_v291;
import com.nukkitx.protocol.bedrock.v313.Bedrock_v313;
import com.nukkitx.protocol.bedrock.v332.Bedrock_v332;
import com.nukkitx.protocol.bedrock.v340.Bedrock_v340;
import com.nukkitx.protocol.bedrock.v354.Bedrock_v354;
import com.nukkitx.protocol.bedrock.v361.Bedrock_v361;
import com.nukkitx.protocol.bedrock.v388.Bedrock_v388;
import com.nukkitx.protocol.bedrock.v389.Bedrock_v389;
import com.nukkitx.protocol.bedrock.v390.Bedrock_v390;
import com.nukkitx.protocol.bedrock.v407.Bedrock_v407;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;
import com.nukkitx.protocol.bedrock.v419.Bedrock_v419;
import com.nukkitx.protocol.bedrock.v422.Bedrock_v422;
import com.nukkitx.protocol.bedrock.v428.Bedrock_v428;
import com.nukkitx.protocol.bedrock.v431.Bedrock_v431;
import com.nukkitx.protocol.bedrock.v440.Bedrock_v440;
import com.nukkitx.protocol.bedrock.v448.Bedrock_v448;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProtocolVersionUtil {
    private static Map<Integer, BedrockPacketCodec> codecMap = new HashMap<>();
    private static Map<BedrockPacketCodec, Integer> rakNetVersionMap = new HashMap<>();

    static {
        registerPacketCodec(Bedrock_v291.V291_CODEC, 9);
        registerPacketCodec(Bedrock_v313.V313_CODEC, 9);
        registerPacketCodec(Bedrock_v332.V332_CODEC, 9);
        registerPacketCodec(Bedrock_v340.V340_CODEC, 9);
        registerPacketCodec(Bedrock_v354.V354_CODEC, 9);
        registerPacketCodec(Bedrock_v361.V361_CODEC, 9);
        registerPacketCodec(Bedrock_v388.V388_CODEC, 9);
        registerPacketCodec(Bedrock_v389.V389_CODEC, 9);
        registerPacketCodec(Bedrock_v390.V390_CODEC, 9);
        registerPacketCodec(Bedrock_v407.V407_CODEC, 10);
        registerPacketCodec(Bedrock_v408.V408_CODEC, 10);
        registerPacketCodec(Bedrock_v419.V419_CODEC, 10);
        registerPacketCodec(Bedrock_v422.V422_CODEC, 10);
        registerPacketCodec(Bedrock_v428.V428_CODEC, 10);
        registerPacketCodec(Bedrock_v431.V431_CODEC, 10);
        registerPacketCodec(Bedrock_v440.V440_CODEC, 10);
        registerPacketCodec(Bedrock_v448.V448_CODEC, 10);
        codecMap = Collections.unmodifiableMap(codecMap);
        rakNetVersionMap = Collections.unmodifiableMap(rakNetVersionMap);
    }

    private static void registerPacketCodec(BedrockPacketCodec codec, int rakNetProtocolVersion) {
        codecMap.put(codec.getProtocolVersion(), codec);
        rakNetVersionMap.put(codec, rakNetProtocolVersion);
    }

    public static BedrockPacketCodec getPacketCodec(int protocolVersion) {
        BedrockPacketCodec codec = codecMap.get(protocolVersion);
        if (codec == null) {
            throw new IllegalArgumentException("Unsupported protocol version: " + protocolVersion);
        }
        return codec;
    }

    public static int getRakNetProtocolVersion(BedrockPacketCodec codec) {
        return rakNetVersionMap.get(codec);
    }
}
