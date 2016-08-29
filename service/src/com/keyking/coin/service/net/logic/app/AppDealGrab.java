package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;
import com.keyking.coin.util.ServerLog;

public class AppDealGrab extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid  = buffer.getLong();
		long id   = buffer.getLong();
		int num   = buffer.getInt();
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
		Deal deal = CTRL.tryToSearch(id);
		if (deal == null){
			resp.setError("找不到交易数据");
			return resp;
		}
		if (!deal.checkValidTime()){
			resp.setError("此贴已过期");
			return resp;
		}
		synchronized (deal) {
			if (deal.getSellFlag() == Deal.DEAL_TYPE_BUY && !user.getPermission().seller()){//这个买帖
				Seller seller = user.getSeller();
				if (seller != null && !seller.isPass()){
					resp.setError("请等待卖家认证通过");
				}else{
					resp.setError("请先进行卖家认证");
				}
				return resp;
			}
			if (!deal.couldGrab(num)){
				resp.setError("剩余数量不足，抢单失败。");
				return resp;
			}
			if (uid == deal.getUid()){
				resp.setError("自己的交易不能抢单");
				return resp;
			}
			DealOrder order = new DealOrder();
			order.setDealId(id);
			order.setBuyId(uid);
			order.addTimes(deal,(byte)0);
			order.setPrice(deal.getPrice());
			order.setNum(num);
			order.setHelpFlag(deal.getHelpFlag());
			long orderId = PK.key(TableName.TABLE_NAME_ORDER);
			order.setId(orderId);
			deal.addOrder(order);
			order.save();
			TransformOrderDetail tod = new TransformOrderDetail();
			tod.copy(deal,order);
			resp.setSucces();
			resp.put("num",deal.getLeftNum());
			resp.put("monad",deal.getMonad());
			resp.put("order",tod);
			ServerLog.info(user.getAccount() + " grab deal ok ----> id is " + id);
		}
		return resp;
	}

}
