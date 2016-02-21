package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.ServerLog;

public class SystemError extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer,String logicName) throws Exception {
		String tip = buffer.getUTF();
		ServerLog.error(tip);
		return null;
	}

}
 
 
