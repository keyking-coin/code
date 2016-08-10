package com.keyking.coin.service.http.handler.impl;

import java.util.List;

import com.keyking.coin.service.domain.bourse.BourseInfo;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;

public class HttpBourseList extends HttpHandler {
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String str = request.getParameter("type");//1只在文交所导航,2文交所导航加下拉列表,3文交所导航加下拉列表加热门文交所
		byte type = Byte.parseByte(str);
		List<BourseInfo> infos = DB.getBourseDao().load(type);
		String result = infos == null ? "[]" : JsonUtil.ObjectToJsonString(infos);
		message(request,response,result);
	}
}
