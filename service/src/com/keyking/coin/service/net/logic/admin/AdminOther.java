package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.tranform.TransformOther;
import com.keyking.coin.util.JsonUtil;

public class AdminOther extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		String str = buffer.getUTF();
		TransformOther other = JsonUtil.JsonToObject(str,TransformOther.class);
		if (other != null){
			resp.setSucces();
		}
		return resp;
	}
}
