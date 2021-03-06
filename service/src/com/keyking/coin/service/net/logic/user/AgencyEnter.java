package com.keyking.coin.service.net.logic.user;

import java.util.List;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.JsonUtil;

public class AgencyEnter extends AbstractLogic {
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String searchStr = buffer.getUTF();
		SearchCondition condition = new SearchCondition();
		if (!searchStr.equals("null")){
			condition = JsonUtil.JsonToObject(searchStr,SearchCondition.class);
		}
		condition.setAgency(true);
		List<Deal> deals = CTRL.getSearchDeals(condition);
		resp.add(deals);
		resp.setSucces();
		return resp;
	}

}
