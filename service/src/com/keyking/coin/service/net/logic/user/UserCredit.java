package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class UserCredit extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp  = new GeneralResp(logicName);
		long id   = buffer.getLong();
		UserCharacter user = CTRL.search(id);
		if (user != null){
			resp.add(user.getCredit());
			resp.setSucces();
		}else{
			resp.setError("找不到用户");
		}
		return resp;
	}

}
