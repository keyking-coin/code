package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;

public class AdminOrderRevoke extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			synchronized (order) {
				if (order != null){
					order.setNeedSave(true);
					order.setRevoke(DealOrder.ORDER_REVOKE_ALL);
					if (order.getHelpFlag() == 1){
						NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG));
					}
					resp.setSucces();
				}
			}
		}
		return resp;
	}

}
