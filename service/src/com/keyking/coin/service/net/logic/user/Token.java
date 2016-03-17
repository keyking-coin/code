package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class Token extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String key    = buffer.getUTF();
		String modlue = buffer.getUTF();
		String code = TOKEN.create(key);
		resp.setSucces();
		if (!SMS.send(modlue,key,code)){
			resp.setError("系统错误 : " + code);
		}
		return resp;
	}
}
 
