package com.joymeng.gm2.net.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.joymeng.gm2.GMServer;



/**
 * 默认的MINA编码工厂
 * @author ShaoLong Wang
 *
 */
public class DefaultGMCodecFactory implements ProtocolCodecFactory
{
	/**
	 * 解码器
	 */
	private ProtocolDecoder decoder;
	
	/**
	 * 编码器
	 */
	private ProtocolEncoder encoder;
	
	
	public DefaultGMCodecFactory(GMServer gmServer)
	{
		decoder = new DefaultGMDecoder(gmServer);
		encoder = new DefaultGMEncoder(gmServer);
	}
	
	
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception
	{
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception
	{
		return encoder;
	}

}
