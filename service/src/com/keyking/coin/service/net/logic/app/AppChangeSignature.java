package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.StringUtil;

public class AppChangeSignature extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();//用户编号
		String signature = buffer.getUTF();//签名内容
		if (StringUtil.isNull(signature)){
			resp.setError("签名内容不能未空");
			return resp;
		}
		UserCharacter user = CTRL.search(uid);
		if (user == null){//不存在账号是account
			resp.setError("找不到用户");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		user.setSignature(signature);
		user.save();
		resp.setSucces();
		return resp;
	}

}
