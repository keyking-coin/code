package com.joymeng.gm2.net.handler;

import org.apache.mina.core.session.IoSession;

import com.joymeng.Instances;
import com.joymeng.gm2.net.message.AbstractGMProtocol;


public abstract class AbstractProtocolHandler<M extends AbstractGMProtocol> implements IProtocolHandler,Instances{
	/**
	 * 强制转换类型的语法糖 
	 * by Shaolong Wang
	 */
	@Override
	public void handle(IoSession session, AbstractGMProtocol message) {
		_handle(session, (M)message);
	}

	protected abstract void _handle(IoSession session, M message);

}
