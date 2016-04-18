package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;

public class AdminConnect extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		resp.setSucces();
		return resp;
	}

}
