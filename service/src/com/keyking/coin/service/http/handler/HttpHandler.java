package com.keyking.coin.service.http.handler;

import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.Instances;

public abstract class HttpHandler implements Instances{
	public abstract void handle(HttpRequestMessage request,HttpResponseMessage response); 
	
	public String formatJosn(HttpRequestMessage request,String src){
		String call = request.getParameter("jsoncallback");
		if (call == null){
			return src;
		}
		String str = call + "(" + src + ")";
		return str;
	}
	
	public void error(HttpRequestMessage request,HttpResponseMessage response,String tips){
		response.appendBody(formatJosn(request,"{\"result\":\"" + tips + "\"}"));
	}
}
