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

public class ProtocolVersionUtil {
    public static BedrockPacketCodec getPacketCodec(int protocolVersion) {
        switch (protocolVersion) {
            case 291:
                return Bedrock_v291.V291_CODEC;
            case 313:
                return Bedrock_v313.V313_CODEC;
            case 332:
                return Bedrock_v332.V332_CODEC;
            case 340:
                return Bedrock_v340.V340_CODEC;
            case 354:
                return Bedrock_v354.V354_CODEC;
            case 361:
                return Bedrock_v361.V361_CODEC;
            case 388:
                return Bedrock_v388.V388_CODEC;
            case 389:
                return Bedrock_v389.V389_CODEC;
            case 390:
                return Bedrock_v390.V390_CODEC;
            case 407:
                return Bedrock_v407.V407_CODEC;
            case 408:
                return Bedrock_v408.V408_CODEC;
            case 419:
                return Bedrock_v419.V419_CODEC;
            case 422:
                return Bedrock_v422.V422_CODEC;
            case 428:
                return Bedrock_v428.V428_CODEC;
            default:
                throw new IllegalArgumentException("Unsupported protocol version: " + protocolVersion);
        }
    }
}
