package com.keyking.coin.service.net.logic.app;

import java.util.List;

import com.keyking.coin.service.domain.bourse.BourseInfo;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppBourseList extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		byte type = buffer.get();//1只在文交所导航,2文交所导航加下拉列表,3文交所导航加下拉列表加热门文交所
		List<BourseInfo> infos = DB.getBourseDao().load(type);
		if (infos == null){
			resp.put("list","[]");
		}else{
			resp.put("list",infos);
		}
		resp.setSucces();
		return resp;
	}

}
