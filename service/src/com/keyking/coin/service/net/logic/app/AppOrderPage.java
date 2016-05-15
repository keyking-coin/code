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
import com.keyking.coin.util.StringUtil;

public class AppOrderPage extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		int page = buffer.getInt();
		int num  = buffer.getInt();
		String type    = buffer.getUTF();
		String bourse  = buffer.getUTF();
		String title   = buffer.getUTF();
		String seller  = buffer.getUTF();
		String buyer   = buffer.getUTF();
		String valid   = buffer.getUTF();
		SearchCondition condition = new SearchCondition();
		if (!StringUtil.isNull(type)){
			condition.setType(type);
		}
		if (!StringUtil.isNull(title)){
			condition.setTitle(title);
		}
		if (!StringUtil.isNull(bourse)){
			condition.setBourse(bourse);
		}
		if (!StringUtil.isNull(seller)){
			condition.setSeller(seller);
		}
		if (!StringUtil.isNull(buyer)){
			condition.setBuyer(buyer);
		}
		if (!StringUtil.isNull(valid)){
			condition.setValid(valid);
		}
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
