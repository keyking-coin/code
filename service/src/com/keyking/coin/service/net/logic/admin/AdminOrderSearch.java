package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.tranform.TransformDealData;

public class AdminOrderSearch extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long ordeId = buffer.getLong();
		TransformDealData deal = CTRL.tryToSearchOrder(ordeId);
		if (deal != null){
			resp.addKey("deal",deal);
			resp.setSucces();
		}else{
			resp.setError("找不到订单编号是：" + ordeId);
		}
		return resp;
	}
}
