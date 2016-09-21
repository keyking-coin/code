package com.joymeng.gm2.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joymeng.gm2.GMServer;
import com.joymeng.gm2.net.message.AbstractGMProtocol;
import com.joymeng.services.core.buffer.JoyBuffer;

class DefaultGMDecoder extends CumulativeProtocolDecoder
{

	static Logger logger = LoggerFactory.getLogger(DefaultGMDecoder.class);
	
	private GMServer gmServer;
	
	public DefaultGMDecoder(GMServer gmServer)
	{
		this.gmServer = gmServer;
	}
	


	
	/**
	 * 解包操作
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception 
	{
		//长度未接受完整
		int remain = in.remaining();
		
		//若协议长度标识尚且不够
		if(remain < AbstractGMProtocol.BYTE_LENGTH)
		{
			return false;
		}
		
		int basePos = in.position();
		int length = in.getInt(basePos + AbstractGMProtocol.POS_LENGTH);
		
		//包体长度不够
		if(length > remain - AbstractGMProtocol.BYTE_LENGTH)
		{
			return false;
		}
		else if(length >= AbstractGMProtocol.MAX_PROTOCOL_LEN)//当包过大时，丢弃该包
		{
			logger.error("received package length beyond " +  AbstractGMProtocol.MAX_PROTOCOL_LEN + " bytes, this package is dropped:" + in.getHexDump());
			//跳过4个字节的包体长度标识和包体长度
			in.position(basePos + AbstractGMProtocol.BYTE_LENGTH + length);
			return true;
		}
		
		//获取protocol
		int messageID = in.getInt(basePos + AbstractGMProtocol.POS_MESSAGE_ID);
		AbstractGMProtocol message = gmServer.getNetMgr().getProtocol(messageID);
	
		if(message == null)
		{
			logger.error("receive unknown message:" + messageID);
			//跳过4个字节的包体长度标识和包体长度
			in.position(basePos + AbstractGMProtocol.BYTE_LENGTH + length);
			return true;
		}

		
		
		//复制
		byte[] destBytes = new byte[AbstractGMProtocol.BYTE_LENGTH + length];
		JoyBuffer destBuffer = JoyBuffer.wrap(destBytes);
		in.get(destBytes, 0, destBytes.length);
	

		try
		{
			message.deserialize(destBuffer);
			out.write(message);
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
			destBuffer.position(0);
			logger.error(e.toString() + ":" + destBuffer.buf().getHexDump());
		}
		
		return true;
	}

}
