package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class AppSendEmail extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long sendId = buffer.getLong();
		String name = buffer.getUTF();
		String theme = buffer.getUTF();
		String content = buffer.getUTF();
		UserCharacter sender = CTRL.search(sendId);
		String forbidStr = sender.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		if (StringUtil.isNull(theme)){
			resp.setError("主题不能为空");
			return resp;
		}
		String time = TimeUtils.nowChStr();
		UserCharacter target = CTRL.searchByAccountOrNickName(name);
		if (sender != null && CTRL.tryToSendEmailToUser(sender,time,theme,content,target)){
			resp.setSucces();
			ServerLog.info(sender.getAccount() + " send eamil to " + target.getAccount());
		}else{
			resp.setError("找不到收件人");
		}
		return resp;
	}

}
