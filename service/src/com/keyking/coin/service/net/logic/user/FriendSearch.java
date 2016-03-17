package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class FriendSearch extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String value = buffer.getUTF();
		UserCharacter user  = CTRL.searchByAccountOrNickName(value);
		if (user != null){
			String resultStr = "1" + "," + user.getNikeName();
			resp.add(resultStr);
		}else{
			resp.add("0");
		}
		resp.setSucces();
		return resp;
	}

}
