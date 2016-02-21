package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.user.AccountApply;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class OpenAccountApply extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp  = new GeneralResp(logicName);
		String bourse = buffer.getUTF();
		String bankName = buffer.getUTF();
		String tel = buffer.getUTF();
		String email = buffer.getUTF();
		//String tokenCode = buffer.getUTF();
		String indentFront = buffer.getUTF();
		String indentBack = buffer.getUTF();
		String bankFront = buffer.getUTF();
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
