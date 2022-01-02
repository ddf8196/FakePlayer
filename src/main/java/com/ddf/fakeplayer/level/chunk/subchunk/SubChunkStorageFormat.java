package com.ddf.fakeplayer.level.chunk.subchunk;

public class SubChunkStorageFormat {
    byte type;
    boolean network;

    public SubChunkStorageFormat(byte raw) {
        setRaw(raw);
    }

    public byte getRaw() {
        byte raw = (byte) (type << 1);
        if (network)
            raw |= 0b00000001;
        return raw;
    }

    public void setRaw(byte raw) {
        network = (raw & 0b00000001) != 0;
        type = (byte) ((raw & 0b11111110) >> 1);
    }
}
