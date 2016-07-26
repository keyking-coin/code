package com.keyking.coin.service.net.logic.app;

import java.util.Map;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.push.PushType;
import com.keyking.coin.util.JsonUtil;

public class AppChangePushSetting extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid     = buffer.getLong();//用户编号
		String str   = buffer.getUTF();//json字符串{"PUSH_TYPE_DEAL":"true","PUSH_TYPE_ORDER":"flase",...,"PUSH_TYPE_EMAIL":"true"}
		UserCharacter user = CTRL.search(uid);
		if (user == null){//不存在账号是account
			resp.setError("找不用户");
		}
		Map<String,String> temp = JsonUtil.JsonToObjectMap(str,String.class,String.class);
		for (String key : temp.keySet()){
			PushType pt = PushType.search(key);
			user.updatePush(pt,temp.get(key).equals("true"));
		}
		user.save();
		resp.setSucces();
		resp.put("result","ok");
		return resp;
	}

}
