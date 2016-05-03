package com.keyking.coin.service.net.logic.app;

import org.apache.mina.core.session.IoSession;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.data.LoginData;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.net.resp.sys.MustLoginAgain;
import com.keyking.coin.util.ServerLog;

public class AppLogin extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		String account  = buffer.getUTF();
		String pwd      = buffer.getUTF();
		UserCharacter user = CTRL.search(account);
		if (user == null){//不存在账号是account
			resp.setError("账号:" + account + "不存在");
		}else{
			if (user.getPwd().equals(pwd)){
				String saveKey = user.getSessionAddress();
				if (saveKey != null && !saveKey.equals(session.getRemoteAddress().toString())){
					IoSession save = NET.search(saveKey);
					if (save != null){
						save.write(new MustLoginAgain());
					}
				}
				resp.put("user",new LoginData(user));
				resp.put("deals",CTRL.getRecentOrders());
				resp.setSucces();
				user.setSessionAddress(session.getRemoteAddress().toString());
				ServerLog.info(account + " login from " + session.getRemoteAddress());
			}else{//密码错误
				resp.setError("密码错误");
			}
		}
		return resp;
	}

}
