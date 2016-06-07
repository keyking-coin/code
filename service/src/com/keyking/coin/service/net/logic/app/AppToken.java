package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppToken extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		String key    = buffer.getUTF();
		String code = TOKEN.check(key);
		if (code == null){
			code = TOKEN.create(key);
		}
		if (!SMS.couldSend(key)){
			resp.setError("系统错误 ");
			return resp;
		}
		if (!SMS.sendToken(key,code)){
			resp.put("result","验证码已发送");
			resp.setSucces();
		}else{
			resp.setError("系统错误 ");
		}
		return resp;
	}

}
