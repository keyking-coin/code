package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.other.InfoList;
import com.keyking.coin.service.domain.other.NoticeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppGetInfoList extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		List<NoticeEntity> result = DB.getNoticeDao().search(3);
		List<InfoList> list = new ArrayList<InfoList>();
		for (int i = 0 ; i < result.size() ; i++){
			NoticeEntity entity = result.get(i);
			InfoList info = new InfoList();
			info.set_time(entity.get_time());
			info.setTime(entity.getTime());
			info.setTitle(entity.getTitle());
			list.add(info);
		}
		resp.put("list",list);
		resp.setSucces();
		return resp;
	}

}
