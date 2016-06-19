package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.EmailList;

public class AppEmailList extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();//用户编号
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			List<Email> emails = user.getEmails();
			List<EmailList> els = new ArrayList<EmailList>();
			for (Email email : emails){
				EmailList el = new EmailList(email);
				els.add(el);
			}
			resp.put("list",els);
			resp.setSucces();
		}else{
			resp.setError("找不到用户");
		}
		return resp;
	}

}
