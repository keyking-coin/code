package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppShareOrder extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long id = buffer.getLong();
		DealOrder order = CTRL.searchOrder(id);
		if (order != null){
			resp.put("result","ok");
			resp.put("url","http://www.521uu.cc/share.php?module=order&id=" + id);
			resp.setSucces();
		}else{
			resp.setError("错误的编号");
		}
		return resp;
	}

}
