package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class AdminUserResetPwd extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long adminId   = buffer.getLong();
		String account = buffer.getUTF();
		UserCharacter user = CTRL.search(adminId);
		if (user != null && user.getPermission().isAdmin()){
			UserCharacter target = CTRL.search(account);
			if (target != null){
				target.setPwd("888888");
				target.setNeedSave(true);
				resp.setSucces("密码重置成888888成功");
			}else{
				resp.setError("找不到用户" + account);
			}
		}else{
			resp.setError(account + "不是管理员账号");
		}
		return resp;
	}

}
