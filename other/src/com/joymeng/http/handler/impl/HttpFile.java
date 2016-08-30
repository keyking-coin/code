package com.joymeng.http.handler.impl;

import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;

public class HttpFile extends HttpHandler {
	String fileName;
	
	public HttpFile(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			dispatcher(fileName,response);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
}
