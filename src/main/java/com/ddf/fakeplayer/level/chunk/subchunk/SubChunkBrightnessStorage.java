package com.ddf.fakeplayer.level.chunk.subchunk;

import com.ddf.fakeplayer.util.BrightnessPair;

public final class SubChunkBrightnessStorage {
    private SubChunkBrightnessStorage.LightPair[] mLight = new LightPair[4096];

    public SubChunkBrightnessStorage() {
        for (int i = 0; i < mLight.length; ++i) {
            mLight[i] = new LightPair((byte) 0);
        }
    }

    public final void reset(boolean maxSkyLight, boolean fullyLit) {
        if ( fullyLit ) {
            for (LightPair lightPair : mLight) {
                lightPair.setRaw((byte) 0xFF);
            }
        } else {
            byte raw = 0;
            if (maxSkyLight)
                raw = (byte) 0xF0;
            for (LightPair lightPair : mLight) {
                lightPair.setRaw(raw);
            }
        }
    }

    public static final class LightPair {
        public /*unsigned __int8*/byte blockLight;
        public /*unsigned __int8*/byte skyLight;

        public LightPair(byte raw) {
            setRaw(raw);
        }

        public byte getRaw() {
            return (byte) ((skyLight & 0x0F) << 4 | blockLight & 0x0F);
        }

        public void setRaw(byte raw) {
            skyLight = (byte) ((raw & 0xF0) >> 4);
            blockLight = (byte) (raw & 0x0F);
        }

        public final boolean isDarkness() {
            return getRaw() == 0;
        }

        public final BrightnessPair toBrightnessPair() {
            return new BrightnessPair(skyLight, blockLight);
        }
    }
}
