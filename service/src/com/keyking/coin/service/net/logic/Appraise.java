package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;

public class Appraise extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		byte type   = buffer.get();
		long dealId = buffer.getLong();
		long orderId = buffer.getLong();
		byte star = buffer.get();
		String value = buffer.getUTF();
		Deal deal = CTRL.tryToSearch(dealId);
		resp.add(type);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				DealAppraise appraise = type == 0 ? order.getSellerAppraise() : order.getBuyerAppraise();
				if (!appraise.isCompleted()){
					appraise.appraise(star,value);
					deal.setNeedSave(true);
					order.setNeedSave(true);
					resp.add(appraise);
					UserCharacter user = CTRL.search(type == 0 ? deal.getUid() : order.getBuyId());
					if (user != null){
						user.getCredit().addNum(star);
						user = CTRL.search(type == 1 ? deal.getUid() : order.getBuyId());
						NET.sendMessageToAllClent(order.clientMessage(Module.UPDATE_FLAG),null);
						ServerLog.info(user.getAccount() + " appraised : star = " + star + " context = " + value);
					}
					resp.setSucces();
				}else{
					resp.setError("已评价过了");
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
