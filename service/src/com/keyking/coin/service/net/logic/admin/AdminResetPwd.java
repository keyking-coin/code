package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.domain.user.UserPermission;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class AdminResetPwd extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String account = buffer.getUTF();
		String code    = buffer.getUTF();
		String pwd     = buffer.getUTF();
		UserCharacter admin = CTRL.search(account);
		if (admin != null){
			UserPermission permission = admin.getPermission();
			if (!permission.isAdmin()){
				resp.setError(account + "不是管理员账号");
				return resp;
			}
			if (!code.equals(permission.getSafeCode())){
				resp.setError("输入的密码保护码错误");
				return resp;
			}
			resp.setSucces();
			admin.setPwd(pwd);
		}else{
			resp.setError("未找到管理员账号是<" + account + ">");
		}
		return resp;
	}

}
