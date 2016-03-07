package com.keyking.coin.service.http.handler;

import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.Instances;

public interface HttpHandler extends Instances{
	public void handle(HttpRequestMessage request,HttpResponseMessage response); 
}
