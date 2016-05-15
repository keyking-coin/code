package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppLookEmail extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();
		long eid = buffer.getLong();//阅读邮件的编号
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		Email email = user.searchEmail(eid);
		if (email != null){
			email.setStatus((byte)1);
			email.save();
			resp.put("num",user.getNewEmailNum());
			resp.setSucces();
		}else{
			resp.setError("找不到邮件");
		}
		return resp;
	}

}
