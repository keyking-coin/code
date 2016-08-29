package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.AccountApply;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppOpenAccount extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			resp.setError("系统错误");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		String bourse = buffer.getUTF();
		String bankName = buffer.getUTF();
		String tel = buffer.getUTF();
		String email = buffer.getUTF();
		String indentFront = buffer.getUTF();
		if (indentFront.startsWith("http://")){
			int index = indentFront.lastIndexOf("/");
			indentFront = indentFront.substring(index + 1,indentFront.length());
		}
		String indentBack = buffer.getUTF();
		if (indentBack.startsWith("http://")){
			int index = indentBack.lastIndexOf("/");
			indentBack = indentBack.substring(index + 1,indentBack.length());
		}
		String bankFront = buffer.getUTF();
		if (bankFront.startsWith("http://")){
			int index = bankFront.lastIndexOf("/");
			bankFront = bankFront.substring(index + 1,bankFront.length());
		}
		AccountApply apply = new AccountApply();
		apply.setBourse(bourse);
		apply.setBankName(bankName);
		apply.setTel(tel);
		apply.setEmail(email);
		apply.setIndentFront(indentFront);
		apply.setIndentBack(indentBack);
		apply.setBankFront(bankFront);
		if (DB.getAccountApplyDao().insert(apply)){
			resp.setSucces();
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
