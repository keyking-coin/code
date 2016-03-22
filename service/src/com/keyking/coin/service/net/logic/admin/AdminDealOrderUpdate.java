package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;

public class AdminDealOrderUpdate extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long dealId = buffer.getLong();
		long orderId = buffer.getLong();
		byte index = buffer.get();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				synchronized (order) {
					order.addTimes(index);
					NET.sendMessageToAllClent(order.clientMessage(Module.UPDATE_FLAG),null);
					NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG));
				}
			}
		}
		return resp;
	}

}
