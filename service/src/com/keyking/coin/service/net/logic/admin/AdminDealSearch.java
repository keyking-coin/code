package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class AdminDealSearch extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		//String account = buffer.getUTF();
		long dealId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		resp.setSucces();
		if (deal != null){
			resp.add(1);
			resp.add(deal);
		}else{
			resp.add(0);
		}
		return resp;
	}

}
