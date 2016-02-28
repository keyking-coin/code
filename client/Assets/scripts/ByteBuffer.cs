using System;
using System.Text;

public class ByteBuffer{
	private byte[] buf;
	private int readIndex = 0;
	private int writeIndex = 0;
	private int markReadIndex = 0;
	private int markWirteIndex = 0;
	private int capacity;
	Encoding encoding = Encoding.UTF8;

	private ByteBuffer(int capacity){
		buf = new byte[capacity];
		this.capacity = capacity;
	}

	private ByteBuffer(byte[] bytes)
	{
		buf = bytes;
		this.capacity = bytes.Length;
	}

	public static ByteBuffer Allocate(int capacity)
	{
		return new ByteBuffer(capacity);
	}

	public static ByteBuffer Allocate(byte[] bytes)
	{
		return new ByteBuffer(bytes);
	}

	private int FixLength(int length) 
	{
		int n = 2;
		int b = 2;
		while( b < length) {
			b = 2 << n;
			n++;
		}
		return b;
	}
	
	public void skip(int len){
		writeIndex += len;
	}
	
	public int getWriteIndex()
	{
		return writeIndex;
	}

	private byte[] flip(byte[] bytes)
	{
		if (BitConverter.IsLittleEndian)
		{
			Array.Reverse(bytes);
		}
		return bytes;
	}

	private int FixSizeAndReset(int currLen, int futureLen)
	{
		if (futureLen > currLen)
		{
			int size = FixLength(currLen) * 2;
			if (futureLen > size)
			{
				size = FixLength(futureLen) * 2;
			}
			byte[] newbuf = new byte[size];
			Array.Copy(buf, 0, newbuf, 0, currLen);
			buf = newbuf;
			capacity = newbuf.Length;
		}
		return futureLen;
	}

	public void WriteBytes(byte[] bytes, int startIndex, int length)
	{
		lock (this)
		{
			int offset = length - startIndex;
			if (offset <= 0) return;
			int total = offset + writeIndex;
			int len = buf.Length;
			FixSizeAndReset(len, total);
			for (int i = writeIndex, j = startIndex; i < total; i++, j++)
			{
				buf[i] = bytes[j];
			}
			writeIndex = total;
		}
	}

	public void WriteBytesTo(byte[] bytes,int length)
	{
		WriteBytesTo (bytes,writeIndex,length);
	}

	public void WriteBytesTo(byte[] bytes, int startIndex, int length)
	{
		lock (this)
		{
			///int len = buf.Length;
			FixSizeAndReset(buf.Length, writeIndex + length);
			for (int i = startIndex, j = 0; j < length ; i++, j++)
			{
				buf[i] = bytes[j];
				if (i > writeIndex){
					writeIndex++;
				}
			}
		}
	}

	public void WriteBytes(byte[] bytes, int length)
	{
		WriteBytes(bytes, 0, length);
	}
	

	public void WriteBytes(byte[] bytes)
	{
		WriteBytes(bytes, bytes.Length);
	}

	public void Write(ByteBuffer buffer)
	{
		if (buffer == null) return;
		if (buffer.ReadableBytes() <= 0) return;
		WriteBytes(buffer.ToArray());
	}

	public void WriteShort(short value)
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteUshort(ushort value)
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteInt(int value)
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteInt(int index, int value)
	{
		byte[] array = flip(BitConverter.GetBytes(value));
		WriteBytesTo(array, index, array.Length);
	}

	public void WriteUint(uint value)
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteUint(int index, uint value)
	{
		byte[] array = flip(BitConverter.GetBytes(value));
		WriteBytesTo(array, index, array.Length);
	}

	public void WriteLong(long value)
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteUlong(ulong value)
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteFloat(float value)
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteByte(byte value)
	{
		lock (this)
		{
			int afterLen = writeIndex + 1;
			int len = buf.Length;
			FixSizeAndReset(len, afterLen);
			buf[writeIndex] = value;
			writeIndex = afterLen;
		}
	}

	public void WriteDouble(double value) 
	{
		WriteBytes(flip(BitConverter.GetBytes(value)));
	}

	public void WriteString(string str) 
	{
		byte[] bytes = encoding.GetBytes (str);
		WriteInt (bytes.Length);
		WriteBytes (bytes);
	}

	public byte ReadByte()
	{
		byte b = buf[readIndex];
		readIndex++;
		return b;
	}

	private byte[] Read(int len)
	{
		byte[] bytes = new byte[len];
		Array.Copy(buf, readIndex,bytes,0,len);
		if (BitConverter.IsLittleEndian)
		{
			Array.Reverse(bytes);
		}
		readIndex += len;
		return bytes;
	}

	public ushort ReadUshort()
	{
		return BitConverter.ToUInt16(Read(2), 0);
	}

	public short ReadShort()
	{
		return BitConverter.ToInt16(Read(2), 0);
	}

	public uint ReadUint()
	{
		return BitConverter.ToUInt32(Read(4), 0);
	}

	public int ReadInt()
	{
		return BitConverter.ToInt32(Read(4), 0);
	}

	public ulong ReadUlong()
	{
		return BitConverter.ToUInt64(Read(8), 0);
	}

	public long ReadLong()
	{
		return BitConverter.ToInt64(Read(8), 0);
	}

	public float ReadFloat()
	{
		return BitConverter.ToSingle(Read(4), 0);
	}

	public double ReadDouble() 
	{
		return BitConverter.ToDouble(Read(8), 0);
	}

	public string ReadString() 
	{
		int len = ReadInt ();
		byte[] bytes = new byte[len];
		ReadBytes (bytes);
		return encoding.GetString(bytes);
	}

	public void ReadBytes(byte[] disbytes)
	{
		int len = disbytes.Length;
		Array.Copy(buf,readIndex,disbytes,0,len);
		readIndex += len;
	}

	public void DiscardReadBytes() 
	{
		if(readIndex <= 0) return;
		int len = buf.Length - readIndex;
		byte[] newbuf = new byte[len];
		Array.Copy(buf, readIndex, newbuf, 0, len);
		buf = newbuf;
		writeIndex -= readIndex;
		markReadIndex -= readIndex;
		if (markReadIndex < 0)
		{
			markReadIndex = readIndex;
		}
		markWirteIndex -= readIndex;
		if (markWirteIndex < 0 || markWirteIndex < readIndex || markWirteIndex < markReadIndex)
		{
			markWirteIndex = writeIndex;
		}
		readIndex = 0;
	}

	public void Clear()
	{
		buf = new byte[buf.Length];
		readIndex = 0;
		writeIndex = 0;
		markReadIndex = 0;
		markWirteIndex = 0;
	}

	public void SetReaderIndex(int index)
	{
		if (index < 0) return;
		readIndex = index;
	}

	public void MarkReaderIndex()
	{
		markReadIndex = readIndex;
	}

	public void MarkWriterIndex() 
	{
		markWirteIndex = writeIndex;
	}

	public void ResetReaderIndex() 
	{
		readIndex = markReadIndex;
	}

	public void ResetWriterIndex() 
	{
		writeIndex = markWirteIndex;
	}

	public int ReadableBytes()
	{
		return writeIndex - readIndex;
	}

	public byte[] ToArray()
	{
		byte[] bytes = new byte[writeIndex];
		Array.Copy(buf, 0, bytes, 0, bytes.Length);
		return bytes;
	}

	public int GetCapacity()
	{
		return this.capacity;
	}
}
 
 
