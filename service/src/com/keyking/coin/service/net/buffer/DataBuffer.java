package com.keyking.coin.service.net.buffer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

public class DataBuffer {
	public static final byte STRING_TYPE_BYTE = 1;
	public static final byte STRING_TYPE_SHORT = 2;
	public static Charset DEFAULT_CHARSET = Charset.UTF_8;
	public static byte DEFAULT_STRING_TYPE = STRING_TYPE_SHORT;
	private IoBuffer buffer;

	public static DataBuffer allocate(int len) {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = IoBuffer.allocate(len);
		joyBuffer.buffer.setAutoExpand(true);
		return joyBuffer;
	}

	public static DataBuffer wrap(ByteBuffer buffer) {
		return wrap(buffer,true);
	}

	public static DataBuffer wrapBuffer(IoBuffer buffer,boolean flag) {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = buffer;
		joyBuffer.buffer.setAutoExpand(flag);
		return joyBuffer;
	}

	public static DataBuffer wrap(ByteBuffer buffer,boolean flag) {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = IoBuffer.wrap(buffer);
		joyBuffer.buffer.setAutoExpand(flag);
		return joyBuffer;
	}

	public static DataBuffer wrap(byte[] datas) {
		return wrap(datas,true);
	}

	public static DataBuffer wrap(byte[] datas, boolean flag) {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = IoBuffer.wrap(ByteBuffer.wrap(datas));
		joyBuffer.buffer.setAutoExpand(flag);
		return joyBuffer;
	}

	public static DataBuffer wrap(byte[] datas, int a,int b) {
		return wrap(datas,a, b,true);
	}

	public static DataBuffer wrap(byte[] datas, int a,int b, boolean flag) {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = IoBuffer.wrap(ByteBuffer.wrap(datas,a, b));
		joyBuffer.buffer.setAutoExpand(flag);
		return joyBuffer;
	}

	public IoBuffer buf() {
		return buffer;
	}

	public int capacity() {
		return buffer.capacity();
	}

	public int position() {
		return buffer.position();
	}

	public DataBuffer position(int index) {
		buffer.position(index);
		return this;
	}

	public int limit() {
		return buffer.limit();
	}

	public DataBuffer limit(int limite) {
		buffer.limit(limite);
		return this;
	}

	public DataBuffer mark() {
		buffer.mark();
		return this;
	}

	public int markValue() {
		return buffer.markValue();
	}

	public DataBuffer reset() {
		buffer.reset();
		return this;
	}

	public DataBuffer clear() {
		buffer.clear();
		return this;
	}

	public DataBuffer flip() {
		buffer.flip();
		return this;
	}

	public DataBuffer rewind() {
		buffer.rewind();
		return this;
	}

	public int remaining() {
		return buffer.remaining();
	}

	public boolean hasRemaining() {
		return buffer.hasRemaining();
	}

	public DataBuffer duplicate() {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = buffer.duplicate();
		return joyBuffer;
	}

	public DataBuffer slice() {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = buffer.slice();
		return joyBuffer;
	}

	public DataBuffer slice(int paramInt) {
		DataBuffer joyBuffer = new DataBuffer();
		joyBuffer.buffer = buffer.slice();
		joyBuffer.limit(paramInt);
		return joyBuffer;
	}

	public DataBuffer sliceNew() {
		return sliceNew(limit() - position());
	}

	public DataBuffer sliceNew(int len) {
		return wrap(array(), position(), len);
	}

	public byte[] array() {
		return buffer.array();
	}

	public byte[] arrayToPosition() {
		int i = position();
		rewind();
		return getByteArray(i);
	}

	public int arrayOffset() {
		return buffer.arrayOffset();
	}

	public byte get() {
		return buffer.get();
	}

	public short getUnsigned() {
		return buffer.getUnsigned();
	}

	public DataBuffer put(byte data) {
		buffer.put(data);
		return this;
	}

	public byte get(int data) {
		return buffer.get(data);
	}

	public short getUnsigned(int paramInt) {
		return buffer.getUnsigned(paramInt);
	}

	public DataBuffer put(int paramInt, byte paramByte) {
		buffer.put(paramInt, paramByte);
		return this;
	}

	public DataBuffer get(byte[] data, int index1, int index2) {
		buffer.get(data, index1, index2);
		return this;
	}

	public DataBuffer get(byte[] data) {
		buffer.get(data);
		return this;
	}

	public byte[] getByteArray(int index) {
		byte[] arrayOfByte = new byte[index];
		get(arrayOfByte);
		return arrayOfByte;
	}

	public DataBuffer put(ByteBuffer buffer) {
		buffer.put(buffer);
		return this;
	}

	public DataBuffer put(byte[] data, int index1, int index2) {
		buffer.put(data, index1, index2);
		return this;
	}

	public DataBuffer put(byte[] data) {
		buffer.put(data);
		return this;
	}

	public DataBuffer compact() {
		buffer.compact();
		return this;
	}

	public ByteOrder order() {
		return buffer.order();
	}

	public DataBuffer order(ByteOrder order) {
		buffer.order(order);
		return this;
	}

	public char getChar() {
		return buffer.getChar();
	}

	public DataBuffer putChar(char ch) {
		buffer.putChar(ch);
		return this;
	}

	public char getChar(int index) {
		return buffer.getChar(index);
	}

	public DataBuffer putChar(int index, char ch) {
		buffer.putChar(index, ch);
		return this;
	}

	public short getShort() {
		return buffer.getShort();
	}

	public int getUnsignedShort() {
		return buffer.getUnsignedShort();
	}

	public DataBuffer putShort(short num) {
		buffer.putShort(num);
		return this;
	}

	public DataBuffer putUnsigned(short num) {
		buffer.put(_toUnsigned(num));
		return this;
	}

	public short getShort(int num) {
		return buffer.getShort(num);
	}

	public int getUnsignedShort(int num) {
		return buffer.getUnsignedShort(num);
	}

	public DataBuffer putShort(int index, short num) {
		buffer.putShort(index, num);
		return this;
	}

	public DataBuffer putUnsigned(int index, short num) {
		buffer.put(index, _toUnsigned(num));
		return this;
	}

	public int getInt() {
		return buffer.getInt();
	}

	public long getUnsignedInt() {
		return buffer.getUnsignedInt();
	}

	public int getMediumInt() {
		return buffer.getMediumInt();
	}

	public int getUnsignedMediumInt() {
		return buffer.getUnsignedMediumInt();
	}

	public int getMediumInt(int num) {
		return buffer.getMediumInt(num);
	}

	public int getUnsignedMediumInt(int num) {
		return buffer.getUnsignedMediumInt(num);
	}

	public DataBuffer putMediumInt(int num) {
		buffer.putMediumInt(num);
		return this;
	}

	public DataBuffer putMediumInt(int index1, int index2) {
		buffer.putMediumInt(index1, index2);
		return this;
	}

	public DataBuffer putInt(int num) {
		buffer.putInt(num);
		return this;
	}

	public DataBuffer putUnsignedShort(int num) {
		buffer.put(_toUnsignedShort(num));
		return this;
	}

	public int getInt(int num) {
		return buffer.getInt(num);
	}

	public long getUnsignedInt(int num) {
		return buffer.getUnsignedInt(num);
	}

	public DataBuffer putUnsignedShort(int index, int num) {
		byte[] data = _toUnsignedShort(num);
		for (int i = 0; i < data.length; ++i){
			buffer.put(index + i, data[i]);
		}
		return this;
	}

	public DataBuffer putInt(int index, int num) {
		buffer.putInt(index, num);
		return this;
	}

	public long getLong() {
		return buffer.getLong();
	}

	public DataBuffer putLong(long num) {
		buffer.putLong(num);
		return this;
	}

	public DataBuffer putUnsignedInt(long num) {
		buffer.put(_toUnsignedInt(num));
		return this;
	}

	public long getLong(int num) {
		return buffer.getLong(num);
	}

	public DataBuffer putLong(int index, long num) {
		buffer.putLong(index, num);
		return this;
	}

	public DataBuffer putUnsignedInt(int index, long num) {
		byte[] data = _toUnsignedInt(num);
		for (int i = 0; i < data.length; ++i){
			buffer.put(index + i, data[i]);
		}
		return this;
	}

	public float getFloat() {
		return buffer.getFloat();
	}

	public DataBuffer putFloat(float num) {
		buffer.putFloat(num);
		return this;
	}

	public float getFloat(int num) {
		return buffer.getFloat(num);
	}

	public DataBuffer putFloat(int index, float num) {
		buffer.putFloat(index, num);
		return this;
	}

	public double getDouble() {
		return buffer.getDouble();
	}

	public DataBuffer putDouble(double num) {
		buffer.putDouble(num);
		return this;
	}

	public double getDouble(int num) {
		return buffer.getDouble(num);
	}

	public DataBuffer putDouble(int index, double num) {
		buffer.putDouble(index, num);
		return this;
	}

	public String getUTF() {
		int len  = getInt();
		return _byteArrayToString(getByteArray(len),DEFAULT_CHARSET);
	}

	public DataBuffer putUTF(String str) {
		byte[] datas = _stringToByteArray(str,DEFAULT_CHARSET);
		putInt(datas.length);
		return put(datas);
	}

	public String getHexDump() {
		return buffer.getHexDump();
	}

	public String getHexDump(int num) {
		return buffer.getHexDump(num);
	}

	public DataBuffer skip(int len) {
		buffer.skip(len);
		return this;
	}

	private String _byteArrayToString(byte[] data,Charset set) {
		try {
			return new String(data, set.charsetName());
		} catch (UnsupportedEncodingException exp) {
			exp.printStackTrace();
		}
		return "";
	}

	private byte[] _stringToByteArray(String str,Charset set) {
		try {
			return str.getBytes(set.charsetName());
		} catch (UnsupportedEncodingException exp) {
			exp.printStackTrace();
		}
		return new byte[0];
	}

	private byte _toUnsigned(short num) {
		return toHH(num, 1)[0];
	}

	private byte[] _toUnsignedShort(int num) {
		return toHH(num, 2);
	}

	private byte[] _toUnsignedInt(long num) {
		return toHH(num, 4);
	}

	public byte[] toHH(long data, int num) {
		byte[] datas = new byte[num];
		for (int i = num - 1; i >= 0; --i){
			datas[i] = (byte) (int) (data >> (num - i - 1) * 8 & 0xFF);
		}
		return datas;
	}
	public enum Charset{
		  UTF_8("UTF-8"),
		  UTF_16("UTF-16"),
		  UTF_16BE("UTF-16BE"),
		  UTF_16LE("UTF-16LE"), 
		  UTF_32("UTF-32"), 
		  UTF_32BE("UTF-32BE"), 
		  UTF_32LE("UTF-32LE"),
		  ISO_8859_1("ISO-8859-1"),
		  US_ASCII("US-ASCII"), 
		  GBK("GBK"), 
		  GB2312("GB2312");

		  private String charsetName;

		  private Charset(String paramString){
		    charsetName = paramString;
		  }

		  public String charsetName(){
		    return charsetName;
		  }
	}
}
 
 
 
