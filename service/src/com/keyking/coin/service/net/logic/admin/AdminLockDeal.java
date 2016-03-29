package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;

public class AdminLockDeal extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long dealId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			for (DealOrder order : deal.getOrders()){
				if (order.getState() == 0){
					order.addRevoke(DealOrder.ORDER_REVOKE_BUYER);
					order.addRevoke(DealOrder.ORDER_REVOKE_SELLER);
					order.setNeedSave(true);
				}
			}
			deal.setNum(deal.orderNum());
			deal.setRevoke(true);
			deal.setNeedSave(true);
			resp.setSucces();
		}else{
			resp.setError("找不到交易编号是:" + dealId);
		}
		return resp;
	}

}
