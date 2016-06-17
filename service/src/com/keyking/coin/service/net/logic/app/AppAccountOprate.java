package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.BankAccount;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.ServerLog;

public class AppAccountOprate extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		byte type = buffer.get();
		long uid = buffer.getLong();
		String name = null;
		String value = null;
		String openName = null;
		String peopleName = null;
		if (type == 0){//0添加
			name = buffer.getUTF();//银行名称
			value = buffer.getUTF();//银行账号
			openName = buffer.getUTF();//开户行地址
			peopleName = buffer.getUTF();//开户人姓名
		}else{//1删除
			name = buffer.getUTF();
		}
		UserCharacter user = CTRL.search(uid);
		if (user == null){//不存在账号是account
			resp.setError("找不到用户");
		}else{
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			BankAccount bankAccount = user.getBankAccount();
			if (type == 0){
				if (!bankAccount.add(name,value,openName,peopleName)){
					resp.setError("此卡已添加过了");
					return resp;
				}
			}else{
				if (!bankAccount.remove(name)){
					resp.setError("未找到要删除的银行卡记录");
					return resp;
				}
			}
			resp.put("account",bankAccount.getAccounts());
			resp.setSucces();
			user.save();
			ServerLog.info(user.getAccount() + " add  BankAccount ----> name is " + name);
		}
		return resp;
	}

}
