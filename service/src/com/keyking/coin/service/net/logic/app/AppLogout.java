package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppLogout extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();//用户编号
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			user.setPlatform(null);
			user.setPushId(null);
			resp.setSucces();
			resp.put("result","ok");
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
