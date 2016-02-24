package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
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
				resp.add(order);
				boolean couldUpdate = false;
				if (index == 1 || index == 3){//买家付款，确认收货
					if ((deal.getSellFlag() == 0 && deal.getUid() == uid) || (deal.getSellFlag() == 1 && order.getBuyId() == uid)){
						couldUpdate = true;
					}
				}else if (index == 2){//卖家发货
					if ((deal.getSellFlag() == 1 && deal.getUid() == uid) || (deal.getSellFlag() == 0 && order.getBuyId() == uid)){
						couldUpdate = true;
					}
				}
				if (couldUpdate){
					byte pre = order.getState();
					order.addTimes(index);
					UserCharacter user = CTRL.search(order.getBuyId());
					NET.sendMessageToClent(deal.clientMessage(Module.UPDATE_FLAG),user);
					resp.setSucces();
					ServerLog.info(CTRL.search(uid).getAccount() + " update deal-order state from " + pre + " to " + order.getState() + " ----> id is " + orderId);
				}else{
					resp.setError("您没有权限那么做");
				}
			}else{
				resp.setError("订单编号错误");
			}
		}else{
			resp.setError("交易已取消");
		}
		return resp;
	}

}
