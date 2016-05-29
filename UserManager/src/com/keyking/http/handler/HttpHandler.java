package com.keyking.http.handler;

import com.keyking.http.request.HttpRequestMessage;
import com.keyking.http.response.HttpResponseMessage;
import com.keyking.util.Instances;

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
	
	public void message(HttpRequestMessage request,HttpResponseMessage response,String tips){
		response.appendBody(formatJosn(request,"{\"result\":\"" + tips + "\"}"));
	}
	
}
