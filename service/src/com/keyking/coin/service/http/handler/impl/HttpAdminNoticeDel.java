package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;

public class HttpAdminNoticeDel extends HttpHandler{
	//http://139.196.30.53:32104/HttpAdminNoticeDel?time=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String time = request.getParameter("time");//标题
		if (DB.getOtherDao().delete(time)){
			message(request,response,"ok");
			ServerLog.info("admin del notice <" + time + ">");
		}else{
			message(request,response,"数据库处理失败");
		}
	}

}
