package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppGetRecentOrders extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		resp.put("deals",CTRL.getRecentOrders());
		resp.setSucces();
		return resp;
	}
}
