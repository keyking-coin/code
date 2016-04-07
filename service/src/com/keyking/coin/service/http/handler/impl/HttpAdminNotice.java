package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.other.NoticeEntity;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpAdminNotice extends HttpHandler {
	//http://139.196.30.53:32104/HttpAdminNotice?title=x&time=x&type=0
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String title = request.getParameter("title");//标题
		String time  = request.getParameter("time");//时间戳
		String type_str  =  request.getParameter("type");//类型
		String body  = request.getPostValue();
		NoticeEntity notice = new NoticeEntity();
		byte type = 0;
		if (StringUtil.isNull(time)){
			time = String.valueOf(TimeUtils.nowLong());
		}
		if (!StringUtil.isNull(type_str)){
			type = Byte.parseByte(type_str);
		}
		if (StringUtil.isNull(title)){
			title = "没有标题";
		}
		notice.setTime(time);
		notice.setBody(body);
		notice.setTitle(title);
		notice.setType(type);
		if (DB.getOtherDao().save(notice)){
			message(request,response,"ok");
			ServerLog.info("admin change notice <" + title + ">");
		}else{
			message(request,response,"添加失败");
		}
	}
}
