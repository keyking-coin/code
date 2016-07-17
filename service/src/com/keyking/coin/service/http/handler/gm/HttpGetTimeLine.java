package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.time.TimeLine;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpGetTimeLine extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request,HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String id = request.getParameter("id");
		TimeLine time = DB.getTimeDao().search(Long.parseLong(id));
		response.put("result","ok");
		response.put("time",time);
	}

	
}
