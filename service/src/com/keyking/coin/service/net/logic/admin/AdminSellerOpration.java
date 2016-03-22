package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.user.PermissionType;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;

public class AdminSellerOpration extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		int type  = buffer.getInt();
		long uid  = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			if (type == 0){
				user.getPermission().setPermission(PermissionType.seller);
				user.setTitle("普通营销员");
				user.getSeller().setPass(true);
			}else{
				user.setSeller(null);
			}
			user.setNeedSave(true);
			NET.sendMessageToAdmin(user.clientAdminMessage(Module.DEL_FLAG));
			NET.sendMessageToClent(user.clientMessage(Module.UPDATE_FLAG),user.getSessionAddress());
			resp.setSucces();
		}
		return resp;
	}

}
