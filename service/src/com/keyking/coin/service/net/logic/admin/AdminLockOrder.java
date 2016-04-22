package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.net.resp.module.AdminModuleResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.tranform.TransformDealData;

public class AdminLockOrder extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				order.addRevoke(DealOrder.ORDER_REVOKE_BUYER);
				order.addRevoke(DealOrder.ORDER_REVOKE_SELLER);
				if (order.getState() == 1 || order.getState() == 4){
					NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG,new AdminModuleResp()));
				}
				order.setNeedSave(true);
				order.save();
				TransformDealData tdd = new TransformDealData();
				tdd.copy(deal,order);
				resp.addKey("deal",tdd);
				resp.setSucces();
			}else{
				resp.setError("找不到订单编号是:" + orderId);
			}
		}else{
			resp.setError("找不到交易编号是:" + dealId);
		}
		return resp;
	}

}
