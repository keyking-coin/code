package com.keyking.coin.service.http.handler;

import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public interface HttpHandler {
	public HttpResponseMessage handle(HttpRequestMessage request); 
}
