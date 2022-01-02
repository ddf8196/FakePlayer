package com.ddf.fakeplayer.util;

public interface IDataInput {
	String readString(); //+2
	String readLongString(); //+3
	float readFloat(); //+4
	double readDouble(); //+5
	byte readByte(); //+6
	short readShort(); //+7
	int readInt(); //+8
	long readLongLong(); //+9
	void readBytes(byte[] b, int offset, int length); //+10
	int numBytesLeft(); //+11

	default byte[] readBytes(int length) {
		byte[] bytes = new byte[length];
		readBytes(bytes, 0, length);
		return bytes;
	}
}
