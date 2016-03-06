package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpLogin implements HttpHandler {

	@Override
	public HttpResponseMessage handle(HttpRequestMessage request) {
		HttpResponseMessage response = new HttpResponseMessage(); 
		String level = request.getParameter("level");  
        response.setContentType("text/plain");  
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);  
        response.appendBody("Hello Http\n");
        response.appendBody("level = " + level); 
        return response;  
	}
}
