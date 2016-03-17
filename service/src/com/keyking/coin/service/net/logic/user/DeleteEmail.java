package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;

public class DeleteEmail extends AbstractLogic {
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		String ids = buffer.getUTF();
		UserCharacter user = CTRL.search(uid);
		if (user != null && user.removeEmail(ids)){
			ServerLog.info(CTRL.search(uid).getAccount() + " delete some eamils ");
			resp.setSucces();
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
