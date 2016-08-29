package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.push.PushType;

public class AppGetPushSetting extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid     = buffer.getLong();//用户编号
		UserCharacter user = CTRL.search(uid);
		if (user == null){//不存在账号是account
			resp.setError("找不用户");
		}
		PushType[] datas = PushType.values();
		for (int i = 0 ; i < datas.length ; i++){
			PushType type = datas[i];
			if (user.pushSend(type)){
				resp.put(type.name(),"true");
			}else{
				resp.put(type.name(),"false");
			}
		}
		resp.setSucces();
		return resp;
	}

}
