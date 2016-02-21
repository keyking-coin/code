package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;

public class FindPassword extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String accout = buffer.getUTF();
		UserCharacter user = CTRL.search(accout);
		if (user != null && SMS.sendPassword(accout,user.getPwd())){
			resp.setSucces();
			ServerLog.info(user.getAccount() + " change passward ok");
		}else{
			resp.setError("手机账号不存在");
		}
		return resp;
	}

}
