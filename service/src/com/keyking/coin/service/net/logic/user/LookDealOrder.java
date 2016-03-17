package com.keyking.coin.service.net.logic.user;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class LookDealOrder extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long dealId  = buffer.getLong();
		long orderId = buffer.getLong();
		List<Deal> deals = new ArrayList<Deal>();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			deals.add(deal);
			resp.setSucces();
		}else{
			resp.setError("系统错误");
		}
		resp.add(deals);
		resp.add(orderId);
		return resp;
	}

}
