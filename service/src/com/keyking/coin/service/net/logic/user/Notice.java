package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.HttpDecoderUtil;

public class Notice extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String result = HttpDecoderUtil.getInfoFromNet("http://www.zgqbyp.com/html/2014-10/20141046538.html");
		if (result != null){
			resp.add(result);
			resp.setSucces();
		}else{
			resp.setError("获取不到数据");
		}
		return resp;
	}
}
