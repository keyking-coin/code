package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.tranform.TransformUserData;
import com.keyking.coin.util.JsonUtil;

public class AdminUserCommit extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		String str = buffer.getUTF();
		TransformUserData userData = JsonUtil.JsonToObject(str, TransformUserData.class);
		UserCharacter user = CTRL.search(userData.getId());
		if (user != null){
			if (!userData.getNikeName().equals(user.getNikeName()) && !CTRL.checkAccout(null,userData.getNikeName(),resp)){
				return resp;
			}
			user.copy(userData);
			userData = new TransformUserData(user);
			resp.addKey("user",userData);
			resp.setSucces();
		}else{
			resp.setError("json数据有错误");
		}
		return resp;
	}

}
