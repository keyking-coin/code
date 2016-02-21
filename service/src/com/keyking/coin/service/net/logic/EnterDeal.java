package com.keyking.coin.service.net.logic;

import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.data.SearchCondition;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.JsonUtil;

public class EnterDeal extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer,String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		resp.setSucces();
		String searchStr = buffer.getUTF();
		List<Deal> deals = null;
		if (searchStr.equals("null")){//普通查询7天内的所有的帖子
			deals = CTRL.getWeekDeals();
		}else{//条件查询
			SearchCondition condition = JsonUtil.JsonToObject(searchStr,SearchCondition.class);
			deals = CTRL.getSearchDeals(condition);
		}
		resp.add(deals);
		return resp;
	}
}
