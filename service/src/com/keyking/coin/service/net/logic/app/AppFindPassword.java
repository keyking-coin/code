package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.ServerLog;

public class AppFindPassword extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		String accout = buffer.getUTF();
		UserCharacter user = CTRL.search(accout);
		if (user != null && SMS.sendPassword(accout,user.getPwd())){
			resp.setSucces();
			resp.put("result","密码已通过短信发送到你手机了");
			ServerLog.info(user.getAccount() + " find  passward by phone");
		}else{
			resp.setError("用户不存在");
		}
		return resp;
	}

}
