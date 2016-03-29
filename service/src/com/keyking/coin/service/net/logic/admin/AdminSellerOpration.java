package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.user.PermissionType;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.net.resp.module.Module;

public class AdminSellerOpration extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		int type  = buffer.getInt();//0通过认证;1拒绝认证(可能是用户的上传资料不对)
		long uid  = buffer.getLong();//用户编号
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			if (type == 0){
				user.getPermission().setType(PermissionType.seller);
				user.setTitle("普通营销员");
				Seller seller = user.getSeller();
				if (seller != null){
					seller.setPass(true);
				}
			}else{
				user.setSeller(null);
			}
			user.setNeedSave(true);
			NET.sendMessageToAdmin(user.clientAdminMessage(Module.DEL_FLAG));
			NET.sendMessageToClent(user.clientMessage(Module.UPDATE_FLAG),user.getSessionAddress());
			resp.setSucces();
		}else{
			resp.setError("找不到用户编号 ：" + uid);
		}
		return resp;
	}
}
