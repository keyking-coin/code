package com.keyking.coin.service.net.logic.app;

import java.util.List;

import com.keyking.coin.service.domain.other.NoticeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppGetNotices extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		List<NoticeEntity> result = DB.getNoticeDao().search(0);
		resp.put("list",result);
		resp.setSucces();
		return resp;
	}
}
