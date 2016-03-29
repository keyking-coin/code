package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.tranform.TransformDealData;

public class AdminDealSearch extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long dealId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			resp.setSucces();
			TransformDealData tdd = new TransformDealData();
			resp.addKey("deal",tdd);
		}else{
			resp.setError("找不到交易 id = " + dealId);
		}
		return resp;
	}

}
