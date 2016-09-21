package com.joymeng.gm2.net.message;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.buffer.JoySerializable;

/**
 * 
 * GM协议基类
 * @author ShaoLong Wang
 * 
 */
public abstract class AbstractGMProtocol implements JoySerializable
{
	
	public static final int POS_LENGTH = 0;
	public static final int BYTE_LENGTH = 4;
	
	
	public static final int POS_PROTOCOL_ID = POS_LENGTH + BYTE_LENGTH;
	public static final int BYTE_PROTOCOL_ID = 1;

	//协议固定头长度
	public static final int FIX_HEAD_LENGTH = POS_PROTOCOL_ID + BYTE_PROTOCOL_ID;//POS_LENGTH + BYTE_LENGTH;
	

	
	public static final int POS_MESSAGE_ID = FIX_HEAD_LENGTH;//FIX_HEAD_LENGTH;
	public static final int BYTE_MESSAGE_ID = 4;
	
	
	
	//协议最大长度50K
	public static final int MAX_PROTOCOL_LEN = 100 * 1024;
	
	//-------------------------------------------------------------------------------------------------------
	//协议类型
	//-------------------------------------------------------------------------------------------------------
	//系统内交互协议
	public static final byte PROTOCOL_SYS = 0;
	//与终端交互协议
	public static final byte PROTOCOL_NORMAL = 1;
	
	//指定的系统服务实例
	public static final int MODULE_SYS = 0xFFFF;
	
	
	private static final long serialVersionUID = -7206757451755700368L;

	/**
	 * 消息体长度
	 */
	protected int length;
	
	/**
	 * protoId兼容其他模式
	 */
	protected byte protoId;
	
	/**
	 * 消息ID
	 */
	protected int messageID;
	
	
	protected AbstractGMProtocol(int messageID)
	{

		this.messageID = messageID;
	}
	
	/**
	 * 编码
	 */
	@Override
	public void serialize(JoyBuffer out)
	{
		out.skip(BYTE_LENGTH);
		out.put(protoId);
		out.putInt(messageID);
		
		_serializeInner(out);
		
		_serialize(out);
		
		//填充长度
		out.putInt(POS_LENGTH, out.position() - BYTE_LENGTH);
	}
	
	/**
	 * 解码
	 */
	@Override
	public void deserialize(JoyBuffer in)
	{
		
		length = in.getInt();
		this.protoId = in.get();
		this.messageID = in.getInt();
		
		_deserializeInner(in);
		
		_deserialize(in);
	}
	
	

	
	
	public int getMessageID() 
	{
		return messageID;
	}


	public void setMessageID(int messageID) 
	{
		this.messageID = messageID;
	}

	
	protected abstract void _serialize(JoyBuffer out);
	
	protected abstract void _deserialize(JoyBuffer in);
	
	
	
	
	
	public abstract void _serializeInner(JoyBuffer out);
	
	public abstract void _deserializeInner(JoyBuffer in);
	
	
}
