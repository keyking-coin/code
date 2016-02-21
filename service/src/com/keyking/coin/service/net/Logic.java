package com.keyking.coin.service.net;

import org.apache.mina.core.session.IoSession;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;


public interface Logic extends Instances{
	
	public Object doLogic(DataBuffer buffer,String logicName) throws Exception;
	
	public void setSession(IoSession session);
	
}
 
 
 
