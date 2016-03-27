package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class Tourist extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		//List<SimpleOrderModule> modules = CTRL.trySearchRecentOrder();
		//resp.add(modules);
		resp.setSucces();
		return resp;
	}

}
