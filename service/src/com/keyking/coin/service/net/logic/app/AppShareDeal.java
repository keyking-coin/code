package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppShareDeal extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long id = buffer.getLong();
		Deal deal = CTRL.tryToSearch(id);
		if (deal != null){
			String module = deal.getSellFlag() == Deal.DEAL_TYPE_SELL ? "sell" : "buy";
			resp.put("url","http://www.521uu.cc/share.php?module=" + module + "&id=" + id);
			resp.setSucces();
		}else{
			resp.setError("错误的编号");
		}
		return resp;
	}

}
