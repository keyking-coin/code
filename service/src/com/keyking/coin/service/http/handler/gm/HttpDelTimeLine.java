package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpDelTimeLine extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long id = Long.parseLong(request.getParameter("id"));
		if (DB.getTimeDao().delete(id)){
			response.put("result","ok");
		}else{
			response.put("result","删除失败");
		}
	}

}
