package com.keyking.coin.service.net.logic;

import org.apache.mina.core.session.IoSession;

import com.keyking.coin.service.net.Logic;

public abstract class AbstractLogic implements Logic {
	
	protected IoSession session;
	
	@Override
	public void setSession(IoSession session) {
		this.session = session;
	}
}
