package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.fb.FeedBack;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.TimeUtils;

public class AppFeedBack extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		String content  = buffer.getUTF();//反馈的信息的内容
		String time     = TimeUtils.nowChStr();
		FeedBack fb     = new FeedBack();
		fb.setTime(time);
		fb.setContent(content);
		if (DB.getFeedBackDao().insert(fb)){
			resp.put("result","ok");
			resp.setSucces();
		}else{
			resp.setError("系统错误，请稍后再试");
		}
		return resp;
	}
}
