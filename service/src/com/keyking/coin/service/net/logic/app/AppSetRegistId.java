package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppSetRegistId extends AbstractLogic {
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid         = buffer.getLong();
		String platform  = buffer.getUTF();
		String registId  = buffer.getUTF();
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			resp.setError("系统错误");
			return resp;
		}
		user.setPushId(registId);
		user.setPlatform(platform);
		resp.put("result","ok");
		resp.setSucces();
		return resp;
	}

}
