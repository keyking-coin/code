package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class AppRegist extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		String account   = buffer.getUTF();
		String code      = buffer.getUTF();
		String pwd       = buffer.getUTF();
		String nickname  = buffer.getUTF();
		String name      = buffer.getUTF();
		String address   = buffer.getUTF();
		String registTime = TimeUtils.nowChStr();
		if (CTRL.checkAccout(account,nickname,resp)){
			int result = TOKEN.check(account,code);
			if (result == 0){
				UserCharacter user = new UserCharacter();
				user.setAccount(account);
				user.setPwd(pwd);
				user.setNikeName(nickname);
				user.setName(name);
				user.addAddress(address);
				user.setRegistTime(registTime);
				if (CTRL.register(user)){
					TOKEN.remove(account);
					user.save();
					resp.setSucces();
					resp.put("result","注册成功");
				}
				user.save();
				ServerLog.info(user.getAccount() + " regist ok at " + registTime);
			}else if (result == 2){
				resp.setError("验证码已失效");
			}else{
				resp.setError("验证码错误");
			}
		}
		return resp;
	}

}
