package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.deal.TransformDealListInfo;
import com.keyking.coin.util.StringUtil;

public class AppDealPage extends AbstractLogic {

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
		List<TransformDealListInfo> dst = new ArrayList<TransformDealListInfo>();
		if (temp.size() > 0){
			List<TransformDealListInfo> src = new ArrayList<TransformDealListInfo>();
			for (Deal deal : temp){
				TransformDealListInfo tdi = new TransformDealListInfo(deal);
				src.add(tdi);
			}
			int left = CTRL.compute(src,dst,page,num);
			resp.put("list",dst);
			resp.put("page",page);
			resp.put("left",left);
		}else{
			resp.put("list",dst);
			resp.put("page",page);
			resp.put("left",0);
		}
		resp.setSucces();
		return resp;
	}
}
