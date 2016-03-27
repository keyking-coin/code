package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.email.EmailModule;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;

public class LookEmail extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		long uid = buffer.getLong();
		long emailId = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			Email email = user.searchEmail(emailId);
			email.setIsNew((byte)1);
			email.setNeedSave(true);
			EmailModule module = new EmailModule();
			module.add("email",email);
			ModuleResp modules = new ModuleResp();
			module.setFlag(Module.UPDATE_FLAG);
			modules.addModule(module);
			NET.sendMessageToClent(modules,user);
		}
		return null;
	}

}
