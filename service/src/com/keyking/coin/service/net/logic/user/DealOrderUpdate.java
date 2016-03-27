package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;

public class DealOrderUpdate extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long dealId = buffer.getLong();
		long orderId = buffer.getLong();
		long uid = buffer.getLong();
		byte index = buffer.get();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				synchronized (order) {
					if (order.checkRevoke()){
						resp.setError("订单已撤销,无法操作");
						return resp;
					}
					resp.add(order);
					boolean couldUpdate = false;
					if (index == order.getState() + 1){
						if (order.getHelpFlag() == 0) {
							if ((index == 1 || index == 3) && (deal.checkBuyer(uid) || order.checkBuyer(deal, uid))) {
								couldUpdate = true;
							}
							if (index == 2 && (deal.checkSeller(uid) || order.checkSeller(deal, uid))){
								couldUpdate = true;
							}
						}else{
							if ((index == 1 || index == 4)&& (deal.checkBuyer(uid) || order.checkBuyer(deal, uid))) {
								couldUpdate = true;
							}
							if (index == 3 && (deal.checkSeller(uid) || order.checkSeller(deal, uid))) {
								couldUpdate = true;
							}
					    }
					}
					if (couldUpdate){
						byte pre = order.getState();
						order.addTimes(index);
						NET.sendMessageToAllClent(order.clientMessage(Module.UPDATE_FLAG),null);
						resp.setSucces();
						if (order.getHelpFlag() == 1){
							if (order.getState() == 1 || order.getState() == 4){
								NET.sendMessageToAdmin(order.clientAdminMessage(Module.ADD_FLAG));
							}else if (pre == 1 || pre == 4){
								NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG));
							}
						}
						ServerLog.info(CTRL.search(uid).getAccount() + " update deal-order state from " + pre + " to " + order.getState() + " ----> id is " + orderId);
					}else{
						resp.setError("您没有权限那么做");
					}
				}
			}else{
				resp.setError("订单编号错误");
			}
		}else{
			resp.setError("找不到交易");
		}
		return resp;
	}

}
