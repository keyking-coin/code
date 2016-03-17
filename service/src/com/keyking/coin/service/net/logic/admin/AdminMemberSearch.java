package com.keyking.coin.service.net.logic.admin;

import java.util.List;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class AdminMemberSearch extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String key = buffer.getUTF();
		List<UserCharacter> targets = CTRL.searchFuzzyUser(key);
		resp.add(targets);
		resp.setSucces();
		return resp;
	}

}
