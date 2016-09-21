package com.joymeng.gm2.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joymeng.gm2.GMServer;
import com.joymeng.gm2.net.message.AbstractGMProtocol;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.exception.JoyException;


/**
 * GM消息编码器
 * @author ShaoLong Wang
 *
 */
class DefaultGMEncoder extends ProtocolEncoderAdapter
{

	static Logger logger = LoggerFactory.getLogger(DefaultGMEncoder.class);
	
	GMServer gmServer;
	
	public DefaultGMEncoder(GMServer gmServer)
	{
		this.gmServer = gmServer;
	}
	
	
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
	{
		
		if(message instanceof AbstractGMProtocol == false)
		{
			throw new JoyException(message.getClass().getName());
		}
		
		AbstractGMProtocol gmMessage = (AbstractGMProtocol)message;
	    
		JoyBuffer joyBuffer = JoyBuffer.allocate(256);
		gmMessage.serialize(joyBuffer);

	    int length = joyBuffer.getInt(AbstractGMProtocol.POS_LENGTH);
		if(length >= AbstractGMProtocol.MAX_PROTOCOL_LEN)
		{
			logger.error("send package length beyond " + AbstractGMProtocol.MAX_PROTOCOL_LEN + " bytes, this package is dropped");
			return;
		}
	
		byte[] buffer = joyBuffer.arrayToPosition();
		out.write(IoBuffer.wrap(buffer));
	}

}
