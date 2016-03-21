package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class AdminOrderRevoke extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				order.setNeedSave(true);
				order.setRevoke(DealOrder.ORDER_REVOKE_ALL);
				resp.setSucces();
			}
		}
		return resp;
	}

}
