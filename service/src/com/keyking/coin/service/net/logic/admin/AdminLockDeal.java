package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class AdminLockDeal extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		int opration  = buffer.getInt();
		long dealId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			deal.setLock(opration == 0);
			deal.setNeedSave(true);
			resp.setSucces();
		}
		return resp;
	}

}
