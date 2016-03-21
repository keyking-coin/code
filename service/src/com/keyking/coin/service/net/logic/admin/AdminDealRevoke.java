package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class AdminDealRevoke extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long dealId = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			String tips = deal.couldDel();
			if (tips != null){
				resp.setError(tips);
				return resp;
			}
			deal.setRevoke(true);
			deal.setNeedSave(true);
			resp.setSucces();
		}
		resp.setSucces();
		return resp;
	}

}
