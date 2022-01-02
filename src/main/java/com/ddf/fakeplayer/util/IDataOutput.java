package com.ddf.fakeplayer.util;

public interface IDataOutput {
	void writeString(String string);
	void writeLongString(String string);
	void writeFloat(float value);
	void writeDouble(double value);
	void writeByte(byte value);
	void writeShort(short value);
	void writeInt(int value);
	void writeLongLong(long value);
	void writeBytes(byte[] bytes, int offset, int length);

	default void writeBytes(byte[] bytes) {
		writeBytes(bytes, 0, bytes.length);
	}
}
