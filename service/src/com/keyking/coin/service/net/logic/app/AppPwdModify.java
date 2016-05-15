package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppPwdModify extends AbstractLogic {
	
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		String account = buffer.getUTF();
		String oldPwd  = buffer.getUTF();
		String newPwd  = buffer.getUTF();
		UserCharacter user = CTRL.search(account);
		if (user != null){
			if (!user.getPwd().equals(oldPwd)){
				resp.setError("旧密码错误");
			}else{
				user.setPwd(newPwd);
				user.save();
				resp.setSucces();
			}
		}else{
			resp.setError("账号不存在");
		}
		return resp;
	}

}
