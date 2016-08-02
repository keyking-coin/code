package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.net.resp.module.AdminModuleResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;

public class AdminDealOrderUpdate extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		byte index   = buffer.get();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				byte state = order.getState();
				ServerLog.info("order state = " + state);
				if (state < index){
					synchronized (order) {
						order.addTimes(deal,index);
						order.save();
						NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG,new AdminModuleResp()));
						resp.addKey("result","操作成功");
						resp.setSucces();
					}
				}else{
					resp.setError("错误的状态修改  : " + index);
				}
			}else{
				resp.setError("错误的订单编号  : " + dealId);
			}
		}else{
			resp.setError("找不到交易,编号是  : " + orderId);
		}
		return resp;
	}

}
