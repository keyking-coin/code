package com.joymeng.gm2.net.handler;

import org.apache.mina.core.session.IoSession;

import com.joymeng.gm2.net.message.AbstractGMProtocol;

public interface IProtocolHandler {
	
	public void handle(IoSession session, AbstractGMProtocol message);

}
