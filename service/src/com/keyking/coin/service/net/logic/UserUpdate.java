package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;

public class UserUpdate extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		String face = buffer.getUTF();
		String signature = buffer.getUTF();
		byte flag = buffer.get();
		String name     = buffer.getUTF();
		String identity = buffer.getUTF();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			user.setFace(face);
			user.setSignature(signature);
			user.setPush(flag);
			if (!StringUtil.isNull(name)){
				user.setName(name);
			}
			if (!StringUtil.isNull(identity)){
				user.setIdentity(identity);
			}
			user.setNeedSave(true);
			resp.setSucces();
			resp.add(user);
			ServerLog.info(user.getAccount() + " update userInfo ");
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
