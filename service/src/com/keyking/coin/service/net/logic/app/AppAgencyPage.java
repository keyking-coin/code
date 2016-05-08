package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.order.TransformOrderListInfo;

public class AppAgencyPage extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		int page = buffer.getInt();
		int num  = buffer.getInt();
		SearchCondition condition = new SearchCondition();
		condition.setAgency(true);
		List<Deal> temp = CTRL.getSearchDeals(condition);
		if (temp.size() > 0){
			List<TransformOrderListInfo> src = new ArrayList<TransformOrderListInfo>();
			List<TransformOrderListInfo> dst = new ArrayList<TransformOrderListInfo>();
			for (Deal deal : temp){
				for (DealOrder order : deal.getOrders()){
					if (order.checkRevoke()){
						continue;
					}
					TransformOrderListInfo tol = new TransformOrderListInfo();
					tol.copy(deal,order);
					src.add(tol);
				}
			}
			Collections.sort(src);
			int left = CTRL.compute(src,dst,page,num);
			resp.put("list",dst);
			resp.put("page",page);
			resp.put("left",left);
		}else{
			resp.put("list","[]");
			resp.put("page",page);
			resp.put("left",0);
		}
		resp.setSucces();
		return resp;
	}

}
