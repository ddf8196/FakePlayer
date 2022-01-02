package com.ddf.fakeplayer.util;

import com.nukkitx.nbt.util.stream.NetworkDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BinaryDataInput extends BytesDataInput {
    private final ByteArrayInputStream bais;
    private final NetworkDataInputStream mStream;

    public BinaryDataInput(byte[] input) {
        this.bais = new ByteArrayInputStream(input);
        this.mStream = new NetworkDataInputStream(bais);
    }

    @Override
    public String readString() {
        try {
            return mStream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotImplemented
    @Override
    public String readLongString() {
        return readString();
    }

    @Override
    public float readFloat() {
        try {
            return mStream.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double readDouble() {
        try {
            return mStream.readDouble();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte readByte() {
        try {
            return mStream.readByte();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short readShort() {
        try {
            return mStream.readShort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int readInt() {
        try {
            return mStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long readLongLong() {
        try {
            return mStream.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readBytes(byte[] b, int offset, int length) {
        try {
            mStream.readFully(b, offset, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int numBytesLeft() {
        return bais.available();
    }
}
