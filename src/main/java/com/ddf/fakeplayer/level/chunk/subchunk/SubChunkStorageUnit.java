package com.ddf.fakeplayer.level.chunk.subchunk;

public class SubChunkStorageUnit {
    public enum Type {
        Paletted1((byte) 0x01, 0b1),
        Paletted2((byte) 0x02, 0b11),
        Paletted3((byte) 0x03, 0b111),
        Paletted4((byte) 0x04, 0b1111),
        Paletted5((byte) 0x05, 0b11111),
        Paletted6((byte) 0x06, 0b111111),
        Paletted8((byte) 0x08, 0b11111111),
        Paletted16((byte) 0x10, 0b111111111111);

        private final byte bitsPerBlock;
        private final int mask;

        Type(byte bitsPerBlock, int mask) {
            this.bitsPerBlock = bitsPerBlock;
            this.mask = mask;
        }

        public int getBitsPerBlock() {
            return bitsPerBlock;
        }

        public int getMask() {
            return mask;
        }

        public static Type getByValue(int value) {
            for (Type type : values()) {
                if (type.getBitsPerBlock() == value) {
                    return type;
                }
            }
            return null;
        }
    }

    public static class SubChunkStorageFormat {
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
}
