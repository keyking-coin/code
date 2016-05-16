package com.keyking.coin.service.http.handler.impl;

import java.util.List;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformTouristOrder;
import com.keyking.coin.util.JsonUtil;

public class HttpTourist extends HttpHandler {
	//http://127.0.0.1:32104/HttpTourist
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		List<TransformTouristOrder> orders = CTRL.trySearchHttpRecentOrder();
		response.appendBody(formatJosn(request,JsonUtil.ObjectToJsonString(orders)));
	}
}
