package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;
import com.keyking.coin.util.ServerLog;

public class AppAppraise extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();//我的编号
		long dealId = buffer.getLong();//交易编号
		long orderId = buffer.getLong();//订单编号
		byte star    = buffer.get();//评价星级
		String context = buffer.getUTF();//评价内容
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			resp.setError("找不到用户");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null) {
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null) {
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
					DealAppraise appraise = null;
					if (deal.checkSeller(uid) || order.checkSeller(deal,uid)){//买家
						appraise = order.getSellerAppraise();
					}else if (deal.checkBuyer(uid) || order.checkBuyer(deal,uid)){//卖家
						appraise = order.getBuyerAppraise();
					}
					if (appraise != null){
						if (!appraise.isCompleted()) {
							appraise.appraise(deal,order,user,star,context);
							order.save();
							UserCharacter other = CTRL.search(uid == deal.getUid() ? order.getBuyId() : deal.getUid());
							if (other != null) {
								other.getCredit().addNum(star);
								other.save();
							}
							TransformOrderDetail tod = new TransformOrderDetail(deal,order);
							resp.setSucces();
							resp.put("order",tod);
							ServerLog.info(user.getAccount()+ " appraised : star = " + star + " context = "+ context);
						}else{
							resp.setError("您已评价过了");
						}
					}else{
						resp.setError("您没有权限评价");
					}
				}
			}else{
				resp.setError("订单编号错误");
			}
		}
		return resp;
	}

}
