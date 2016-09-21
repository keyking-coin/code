package com.joymeng.gm2.net.message;

import com.joymeng.services.core.buffer.JoyBuffer;


/**
 * 
 * 默认指令数据结构
 * 此类中的接口保留自 网游中暂时使用的指令
 * 指令 = 是否需要等待(4bit) 是否服务器发送指令(4bit) 服务器大模块(8bit) 服务器小模块(16bit)
 * 可以按照需求任意修改，弃用该类中的标识即可
 * 协议基类
 * 
 * @author ShaoLong Wang
 * 
 */

public abstract class GMRequest extends AbstractGMProtocol
{

	private static final long serialVersionUID = -8966257280000692731L;


	public GMRequest(int commandID)
	{
		super(commandID);
	}

	
	public final void _serializeInner(JoyBuffer out)
	{
		
	}
	
	public final void _deserializeInner(JoyBuffer in)
	{
		
	}
	

	@Override
	protected final void _serialize(JoyBuffer out) {

	}
	
}
