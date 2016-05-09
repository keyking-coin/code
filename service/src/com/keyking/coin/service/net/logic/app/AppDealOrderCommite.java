package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.net.resp.module.AdminModuleResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;
import com.keyking.coin.util.ServerLog;

public class AppDealOrderCommite extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid     = buffer.getLong();
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		byte state   = buffer.get();
		int num      = buffer.getInt();
		UserCharacter user = CTRL.search(uid);
		if (user == null){//不存在账号是account
			resp.setError("找不到用户");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal == null){
			resp.setError("找不到交易数据");
			return resp;
		}
		DealOrder order = deal.searchOrder(orderId);
		if (order != null){
			synchronized (order) {
				if (order.checkRevoke(3)){
					resp.setError("订单已撤销,无法操作");
					return resp;
				}
				boolean couldUpdate = false;
				if (state == order.getState() + 1){
					if (order.getHelpFlag() == 0) {
						if ((state == 1 || state == 3) && (deal.checkBuyer(uid) || order.checkBuyer(deal, uid))) {
							couldUpdate = true;
						}
						if (state == 2 && (deal.checkSeller(uid) || order.checkSeller(deal, uid))){
							couldUpdate = true;
						}
					}else{
						if ((state == 1 || state == 4)&& (deal.checkBuyer(uid) || order.checkBuyer(deal, uid))) {
							couldUpdate = true;
						}
						if (state == 3 && (deal.checkSeller(uid) || order.checkSeller(deal, uid))) {
							couldUpdate = true;
						}
				    }
				}
				if (couldUpdate) {
					if ((order.getHelpFlag() == 0 && state == 2) || (order.getHelpFlag() == 1 && state == 3)) {
						order.setSellerNum(num);
					}else if ((order.getHelpFlag() == 0 && state == 3) || (order.getHelpFlag() == 1 && state == 4)) {
						order.setBuyerNum(num);
					}
					byte pre = order.getState();
					order.addTimes(state);
					order.save();
					NET.sendMessageToAllClent(order.clientMessage(Module.UPDATE_FLAG), null);
					if (order.getHelpFlag() == 1) {
						if (order.getState() == 1 || order.getState() == 4){
							NET.sendMessageToAdmin(order.clientAdminMessage(Module.ADD_FLAG,new AdminModuleResp()));
						}else if(pre == 1 || pre == 4){
							NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG,new AdminModuleResp()));
						}
					}
					TransformOrderDetail tod = new TransformOrderDetail();
					tod.copy(deal,order);
					resp.put("order",tod);
					resp.setSucces();
					ServerLog.info(CTRL.search(uid).getAccount()+ " update deal-order state from " + pre + " to " + order.getState() + " ----> id is " + orderId);
				}else{
					resp.setError("您没有权限那么做");
				}
			}
		}
		return resp;
	}

}
