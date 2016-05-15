package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppDelEmail extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();
		String ids = buffer.getUTF();//要删除的邮件的编号id(支持删除多个id1,id2,id3...idn)
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		if (user.removeEmail(ids)){
			resp.put("num",user.getNewEmailNum());
			resp.setSucces();
		}else{
			resp.setError("删除失败");
		}
		return resp;
	}

}
