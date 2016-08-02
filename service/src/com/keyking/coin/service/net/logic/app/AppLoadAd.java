package com.keyking.coin.service.net.logic.app;

import java.util.List;

import com.keyking.coin.service.domain.ad.ADEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppLoadAd extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		try {
			List<ADEntity> ads = DB.getAdDao().load();
			resp.put("list",ads == null ? "[]" : ads);
			resp.setSucces();
		} catch (Exception e) {
			e.printStackTrace();
			resp.setError("后台没有配置");
		}
		return resp;
	}

}
