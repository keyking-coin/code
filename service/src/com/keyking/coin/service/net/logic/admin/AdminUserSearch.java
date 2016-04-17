package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.tranform.TransformUserData;

public class AdminUserSearch extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		String searchStr = buffer.getUTF();
		UserCharacter user = CTRL.searchByAccountOrNickName(searchStr);
		if (user != null){
			TransformUserData tud = new TransformUserData(user);
			resp.addKey("user",tud);
			resp.setSucces();
		}else{
			resp.setError("找不到用户" + searchStr);
		}
		return resp;
	}

}
