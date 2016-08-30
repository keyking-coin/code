package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.log.NewLogManager;

public class HttpClientError extends AbstractHandler{
	//218.104.71.178:12121/info/HttpInfo?logic=HttpClientError&clientError=？
	@Override
	public String logic(HttpRequestMessage request) {
		String clientError = request.getParameter("clientError");
		NewLogManager.clientLog(clientError);
		return "Record client error information complete";
	}
}
