package com.ddf.fakeplayer.util.mc;

import com.ddf.fakeplayer.level.chunk.LevelChunkFormat;
import com.ddf.fakeplayer.level.chunk.subchunk.SubChunkFormat;

public class SharedConstants {
    public static final int MajorVersion = 1;
    public static final int MinorVersion = 0x0E;
    public static final int PatchVersion = 0x3C;
    public static final int RevisionVersion = 5;
    public static final boolean IsBeta = false;
    public static final int NetworkProtocolVersion = 0x86;
    public static final int StoreVersion = 1;
    public static final int AutomationProtocolVersion = 1;
    public static final int CompanionAppProtocolVersion = 4;
    public static final int MaxChatLength = 0x64;
    public static final int LevelDBCompressorID = 0x10;
    public static final int CurrentStorageVersion = 8;
    public static final LevelChunkFormat CurrentLevelChunkFormat = LevelChunkFormat.v1_12_0;
    public static final SubChunkFormat CurrentSubChunkFormat = SubChunkFormat.v1_3_0_2;
    public static final int NetworkDefaultGamePort = 0x0BC;
    public static final int NetworkDefaultGamePortv6 = 0x0BD;
    public static final int NetworkEphemeralPort = 0;
    public static final int NetworkDefaultMaxConnections = 0x1E;

    public static int getVersionCode() {
        return SharedConstants.getVersionCode(1, 14, 60, 5);
    }

    public static int getVersionCode(int major, int minor, int patch, int revision) {
        return revision + 1000 * patch + 100000 * minor + 10000000 * major;
    }

    public static boolean isVersion(int major, int minor, int patch, int revision) {
        if ( major == 1 && minor == 14 && patch == 60 && revision == 5) {
            return true;
        }
        return false;
    }
}
