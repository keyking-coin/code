package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpSMS extends HttpHandler {
	//http://139.196.30.53:32104/HttpSMS?tel=13856094894
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String key    = request.getParameter("tel");
		String code = TOKEN.check(key);
		if (code == null){
			code = TOKEN.create(key);
		}
		if (SMS.sendToken(key,code)){
			message(request,response,"ok");
		}else{
			message(request,response,"系统错误 ");
		}
	}
}
