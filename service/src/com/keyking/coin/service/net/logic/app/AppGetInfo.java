package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.other.NoticeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppGetInfo extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long _time = buffer.getLong();
		NoticeEntity entity = DB.getNoticeDao().search(_time);
		if (entity != null){
			resp.put("data",entity);
			resp.setSucces();
		}else{
			resp.setError("找不到资讯内容");
		}
		return resp;
	}

}
