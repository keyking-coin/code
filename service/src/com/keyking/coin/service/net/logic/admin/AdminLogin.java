package com.keyking.coin.service.net.logic.admin;

import org.apache.mina.core.session.IoSession;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.sys.MustLoginAgain;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class AdminLogin extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String account = buffer.getUTF();
		String pwd     = buffer.getUTF();
		UserCharacter user = CTRL.login(account,pwd,resp);
		if (user != null){
			String saveKey = user.getSessionAddress();
			if (saveKey != null && !saveKey.equals("UU_ADMIN" + session.getRemoteAddress().toString())){
				IoSession save = NET.search(saveKey);
				if (save != null){
					save.write(new MustLoginAgain());
				}
			}
			user.setSessionAddress("UU_ADMIN" + session.getRemoteAddress().toString());
			ServerLog.info("admin<" + account + "> login at " + TimeUtils.nowChStr());
		}
		return resp;
	}

}
