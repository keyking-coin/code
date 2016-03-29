package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.tranform.TransformDealData;

public class AdminLockDeal extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long dealId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			deal.setNum(0);
			deal.setRevoke(true);
			deal.setNeedSave(true);
			TransformDealData tdd = new TransformDealData();
			tdd.copy(deal);
			resp.addKey("deal",tdd);
			resp.setSucces();
		}else{
			resp.setError("找不到交易编号是:" + dealId);
		}
		return resp;
	}

}
