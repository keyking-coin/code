package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.email.EmailModule;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.ModuleResp;
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
		UserCharacter target = CTRL.searchByAccountOrNickName(name);
		if (target != null){
			Email email = new Email();
			email.setSenderId(sendId);
			email.setUserId(target.getId());
			String time = TimeUtils.nowChStr();
			email.setTime(time);
			email.setTheme(theme);
			email.setContent(content);
			long id = PK.key("email");
			email.setId(id);
			target.addEmail(email);
			EmailModule module = new EmailModule();
			module.add("eamil",email);
			ModuleResp modules = new ModuleResp();
			modules.addModule(module);
			NET.sendMessageToClent(modules,target);
			resp.setSucces();
			ServerLog.info(CTRL.search(sendId).getAccount() + " send eamil to " + target.getAccount());
		}else{
			resp.setError("找不到收件人");
		}
		return resp;
	}

}
