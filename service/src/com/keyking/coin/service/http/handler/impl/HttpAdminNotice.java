package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.other.NoticeEntity;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpAdminNotice extends HttpHandler {
	//http://139.196.30.53:32104/HttpAdminNotice?title=x&time=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String title = request.getParameter("title");//标题
		String time  = request.getParameter("time");//时间戳
		String body = request.getPostValue();
		NoticeEntity notice = new NoticeEntity();
		if (StringUtil.isNull(time)){
			time = String.valueOf(TimeUtils.nowLong());
		}
		notice.setTime(time);
		notice.setBody(body);
		notice.setTitle(title);
		if (DB.getOtherDao().save(notice)){
			message(request,response,"ok");
			ServerLog.info("admin change notice <" + title + ">");
		}else{
			message(request,response,"添加失败");
		}
	}
}
