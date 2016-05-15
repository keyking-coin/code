package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;

public class AppGetOrder extends AbstractLogic {
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			TransformOrderDetail tod = new TransformOrderDetail();
			tod.copy(deal,order);
			resp.put("deal",tod);
			resp.setSucces();
		}else{
			resp.setError("找不到具体的数据");
		}
		return resp;
	}
}
