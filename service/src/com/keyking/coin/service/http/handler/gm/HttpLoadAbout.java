package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpLoadAbout extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		CTRL.loadAbout();
		response.put("result","load ok");
	}
}
