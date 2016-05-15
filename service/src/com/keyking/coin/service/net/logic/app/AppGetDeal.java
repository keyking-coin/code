package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.deal.TransformDealDetail;

public class AppGetDeal extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long dealId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			TransformDealDetail td = new TransformDealDetail();
			td.copy(deal);
			resp.put("deal",td);
			resp.setSucces();
		}else{
			resp.setError("找不到具体的数据");
		}
		return resp;
	}

}
