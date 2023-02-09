package com.ddf.fakeplayer.util;

import com.ddf.fakeplayer.Resources;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.util.stream.LittleEndianDataInputStream;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
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
import com.nukkitx.protocol.bedrock.v465.Bedrock_v465;
import com.nukkitx.protocol.bedrock.v471.Bedrock_v471;
import com.nukkitx.protocol.bedrock.v475.Bedrock_v475;
import com.nukkitx.protocol.bedrock.v486.Bedrock_v486;
import com.nukkitx.protocol.bedrock.v503.Bedrock_v503;
import com.nukkitx.protocol.bedrock.v527.Bedrock_v527;
import com.nukkitx.protocol.bedrock.v534.Bedrock_v534;
import com.nukkitx.protocol.bedrock.v544.Bedrock_v544;
import com.nukkitx.protocol.bedrock.v545.Bedrock_v545;
import com.nukkitx.protocol.bedrock.v554.Bedrock_v554;
import com.nukkitx.protocol.bedrock.v557.Bedrock_v557;
import com.nukkitx.protocol.bedrock.v560.Bedrock_v560;
import com.nukkitx.protocol.bedrock.v567.Bedrock_v567;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolVersionUtil {
    private static Map<Integer, BedrockPacketCodec> codecMap = new HashMap<>();
    private static Map<BedrockPacketCodec, Integer> rakNetVersionMap = new HashMap<>();
    private static Map<BedrockPacketCodec, List<NbtMap>> blockPaletteMap = new HashMap<>();
    private static BedrockPacketCodec latestPacketCodec;

    static {
        //因皮肤格式问题，不支持1.13及以下版本(disconnectionScreen.invalidSkin)
//        registerPacketCodec(Bedrock_v291.V291_CODEC, 9, null);
//        registerPacketCodec(Bedrock_v313.V313_CODEC, 9, null);
//        registerPacketCodec(Bedrock_v332.V332_CODEC, 9, null);
//        registerPacketCodec(Bedrock_v340.V340_CODEC, 9, null);
//        registerPacketCodec(Bedrock_v354.V354_CODEC, 9, null);
//        registerPacketCodec(Bedrock_v361.V361_CODEC, 9, null);
//        registerPacketCodec(Bedrock_v388.V388_CODEC, 9, null);
        registerPacketCodec(Bedrock_v389.V389_CODEC, 9, null);
        registerPacketCodec(Bedrock_v390.V390_CODEC, 9, null);
        registerPacketCodec(Bedrock_v407.V407_CODEC, 10, null);
        registerPacketCodec(Bedrock_v408.V408_CODEC, 10, null);
        registerPacketCodec(Bedrock_v419.V419_CODEC, 10, "blockpalette_1.16.100.04_v419.nbt");
        registerPacketCodec(Bedrock_v422.V422_CODEC, 10, "blockpalette_1.16.201.03_v422.nbt");
        registerPacketCodec(Bedrock_v428.V428_CODEC, 10, "blockpalette_1.16.210.06_v428.nbt");
        registerPacketCodec(Bedrock_v431.V431_CODEC, 10, "blockpalette_1.16.221.01_v431.nbt");
        registerPacketCodec(Bedrock_v440.V440_CODEC, 10, null);
        registerPacketCodec(Bedrock_v448.V448_CODEC, 10, "blockpalette_1.17.11.01_v448.nbt");
        registerPacketCodec(Bedrock_v465.V465_CODEC, 10, "blockpalette_1.17.34.02_v465.nbt");
        registerPacketCodec(Bedrock_v471.V471_CODEC, 10, "blockpalette_1.17.41.01_v471.nbt");
        registerPacketCodec(Bedrock_v475.V475_CODEC, 10, "blockpalette_1.18.2.03_v475.nbt");
        registerPacketCodec(Bedrock_v486.V486_CODEC, 10, null);
        registerPacketCodec(Bedrock_v503.V503_CODEC, 10, null);
        registerPacketCodec(Bedrock_v527.V527_CODEC, 10, null);
        registerPacketCodec(Bedrock_v534.V534_CODEC, 10, null);
        registerPacketCodec(Bedrock_v544.V544_CODEC, 10, null);
        registerPacketCodec(Bedrock_v545.V545_CODEC, 10, null);
        registerPacketCodec(Bedrock_v554.V554_CODEC, 11, null);
        registerPacketCodec(Bedrock_v557.V557_CODEC, 11, null);
        registerPacketCodec(Bedrock_v560.V560_CODEC, 11, null);
        registerPacketCodec(Bedrock_v567.V567_CODEC, 11, null);
        //registerPacketCodec(ddI);

        codecMap = Collections.unmodifiableMap(codecMap);
        rakNetVersionMap = Collections.unmodifiableMap(rakNetVersionMap);
        blockPaletteMap = Collections.unmodifiableMap(blockPaletteMap);
    }

    private static void registerPacketCodec(BedrockPacketCodec codec, int rakNetProtocolVersion, String blockPalette) {
        codecMap.put(codec.getProtocolVersion(), codec);
        rakNetVersionMap.put(codec, rakNetProtocolVersion);
        if (blockPalette != null) {
            try (NBTInputStream nbtInputStream = new NBTInputStream(new LittleEndianDataInputStream(Resources.getResAsStream("/blockpalette/" + blockPalette)))) {
                Object tag = nbtInputStream.readTag();
                if (tag instanceof NbtMap) {
                    blockPaletteMap.put(codec, ((NbtMap) tag).getList("blocks", NbtType.COMPOUND));
                }
            } catch (IOException ignored) {
            }
        }
        latestPacketCodec = codec;
    }

    public static BedrockPacketCodec getPacketCodec(int protocolVersion) {
        BedrockPacketCodec codec = codecMap.get(protocolVersion);
        if (codec == null) {
            throw new IllegalArgumentException("Unsupported protocol version: " + protocolVersion);
        }
        return codec;
    }

    public static List<NbtMap> getBlockPalette(BedrockPacketCodec codec) {
        return blockPaletteMap.get(codec);
    }

    public static int getRakNetProtocolVersion(BedrockPacketCodec codec) {
        return rakNetVersionMap.get(codec);
    }

    public static BedrockPacketCodec getLatestPacketCodec() {
        return latestPacketCodec;
    }
}
