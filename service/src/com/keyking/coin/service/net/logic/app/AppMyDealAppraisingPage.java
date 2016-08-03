package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.order.TransformOrderListInfo;

public class AppMyDealAppraisingPage extends AbstractLogic {
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid  = buffer.getLong();//用户编号
		int page  = buffer.getInt();//页数
		int num   = buffer.getInt();//一页显示的数据条数
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			resp.setError("找不到用户");
		}else{
			List<TransformOrderListInfo> src = searchConfirmOrders(uid);
			List<TransformOrderListInfo> dst = new ArrayList<TransformOrderListInfo>();
			int left = CTRL.compute(src,dst,page,num);
			resp.put("list",dst);
			resp.put("page",page);
			resp.put("left",left);
			resp.setSucces();
		}
		return resp;
	}
	
	private List<TransformOrderListInfo> searchConfirmOrders(long uid){
		List<TransformOrderListInfo> result = new ArrayList<TransformOrderListInfo>();
		List<Deal> deals = CTRL.getDeals();
		for (int i = 0 ; i < deals.size() ; i++){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				continue;
			}
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					boolean flag1 = deal.checkBuyer(uid) && !order.getBuyerAppraise().isCompleted();
					boolean flag2 = deal.checkSeller(uid) && !order.getSellerAppraise().isCompleted();
					if (flag1 && flag2 && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo();
						hd.copy(deal,order);
						result.add(hd);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isConfirming(deal,uid) && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo();
						hd.copy(deal,order);
						result.add(hd);
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
}
