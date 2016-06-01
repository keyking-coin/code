package com.keyking.coin.service.net.logic.admin;

import java.util.List;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.tranform.TransformUserData;

public class AdminUserLoad extends AbstractLogic {
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		List<TransformUserData> users = CTRL.getCoinUsers();
		resp.addKey("users",users);
		resp.setSucces();
		return resp;
	}

}
