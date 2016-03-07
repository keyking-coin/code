package com.keyking.coin.service.http.handler.impl;

import java.io.FileInputStream;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpLogin implements HttpHandler {

	@Override
	public HttpResponseMessage handle(HttpRequestMessage request) {
		HttpResponseMessage response = new HttpResponseMessage(); 
		//String level = request.getParameter("level");  
        //response.setContentType("text/plain");  
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);  
        //response.appendBody("Hello Http\n");
        //response.appendBody("level = " + level); 
        try {
			FileInputStream fis = new FileInputStream("regist.html");
			byte[] buffer = new byte[1024];
	        int i = -1;
	        while ((i = fis.read(buffer)) != -1) {
	        	response.appendBody(buffer,0,i);
	        }
	        fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return response;  
	}
}
