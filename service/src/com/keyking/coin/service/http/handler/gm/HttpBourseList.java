package com.keyking.coin.service.http.handler.gm;

import java.util.List;

import com.keyking.coin.service.domain.bourse.BourseInfo;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpBourseList extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		List<BourseInfo> list = DB.getBourseDao().load();
		response.put("result","ok");
		response.put("list",list == null ? "[]" : list);
	}
}
