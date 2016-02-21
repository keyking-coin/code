package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.user.BankAccount;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
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
				resp.setError(forbidStr);
				return resp;
			}
			BankAccount bankAccount = user.getBankAccount();
			bankAccount.add(name,value,openName,peopleName);
			resp.add(bankAccount);
			resp.setSucces();
			user.setNeedSave(true);
			ServerLog.info(user.getAccount() + " add  BankAccount ----> name is " + name);
		}
		return resp;
	}

}
