package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;

public class Appraise extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		byte type    = buffer.get();
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		long uid     = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		byte star = buffer.get();
		String value = buffer.getUTF();
		Deal deal = CTRL.tryToSearch(dealId);
		resp.add(type);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				if (!order.over()){
					resp.setError("未完成交易,无法评价");
					return resp;
				}
				synchronized (order) {
					if (order.checkRevoke()) {
						resp.setError("订单已撤销,无法评价");
						return resp;
					}
					boolean couldAppraise = false;
					if (type ==0 && (deal.checkBuyer(uid) || order.checkBuyer(deal,uid))){//我是买家
						couldAppraise = true;
					}
					if (type == 1 && (deal.checkSeller(uid) || order.checkSeller(deal,uid))){//我是卖家
						couldAppraise = true;
					}
					if (couldAppraise){
						DealAppraise appraise = type == 0 ? order.getSellerAppraise() : order.getBuyerAppraise();
						if (!appraise.isCompleted()){
							appraise.appraise(star,value);
							deal.setNeedSave(true);
							order.setNeedSave(true);
							resp.add(appraise);
							UserCharacter other = CTRL.search(uid == deal.getUid() ? order.getBuyId() : deal.getUid());
							if (other != null){
								other.getCredit().addNum(star);
							}
							NET.sendMessageToAllClent(order.clientMessage(Module.UPDATE_FLAG),null);
							ServerLog.info(user.getAccount() + " appraised : star = " + star + " context = " + value);
							resp.setSucces();
						}else{
							resp.setError("已评价过了");
						}
					}else{
						resp.setError("您没有权限评价");
					}
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
