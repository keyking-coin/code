package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class SendEmail extends AbstractLogic {
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long sendId = buffer.getLong();
		String name = buffer.getUTF();
		String theme = buffer.getUTF();
		String content = buffer.getUTF();
		String time = TimeUtils.nowChStr();
		UserCharacter target = CTRL.searchByAccountOrNickName(name);
		UserCharacter sender = CTRL.search(sendId);
		if (CTRL.tryToSendEmailToUser(sender,time,theme,content,target)){
			resp.setSucces();
			ServerLog.info(CTRL.search(sendId).getAccount() + " send eamil to " + target.getAccount());
		}else{
			resp.setError("找不到收件人");
		}
		return resp;
	}

}
