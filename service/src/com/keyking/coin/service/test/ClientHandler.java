package com.keyking.coin.service.test;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class ClientHandler extends IoHandlerAdapter {

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		super.messageReceived(session, message);
	}

	@Override
	public void sessionCreated(IoSession iosession) throws Exception {
		super.sessionCreated(iosession);
	}
}
