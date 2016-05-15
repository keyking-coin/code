package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.BankAccount;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;

public class AddAccount extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		String name = buffer.getUTF();
		String value = buffer.getUTF();
		String openName = buffer.getUTF();
		String peopleName = buffer.getUTF();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			BankAccount bankAccount = user.getBankAccount();
			if (!bankAccount.add(name,value,openName,peopleName)){
				resp.setError("此卡已添加过了");
				return resp;
			}
			resp.add(bankAccount);
			resp.setSucces();
			ServerLog.info(user.getAccount() + " add  BankAccount ----> name is " + name);
		}
		return resp;
	}

}
