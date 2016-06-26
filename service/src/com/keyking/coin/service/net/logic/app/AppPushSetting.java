package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;

public class AppPushSetting extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		long uid    = buffer.getLong();//用户编号
		String  str = buffer.getUTF();//参数josn字符串
		
		return null;
	}

}
