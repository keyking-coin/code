package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class Token extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String key    = buffer.getUTF();
		buffer.getUTF();
		String code = TOKEN.check(key);
		if (code == null){
			code = TOKEN.create(key);
		}
		resp.setSucces();
		if (!SMS.sendToken(key,code)){
			resp.setError("系统错误 ");
		}
		return resp;
	}
}
 
