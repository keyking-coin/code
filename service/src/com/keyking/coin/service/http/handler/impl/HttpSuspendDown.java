package com.keyking.coin.service.http.handler.impl;

import java.io.FileInputStream;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.buffer.DataBuffer;

public class HttpSuspendDown extends HttpHandler {
	//http://139.196.30.53:32104/HttpSuspendDown
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		try {
			response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			FileInputStream fis = new FileInputStream("./suspend.data");
			DataBuffer buffer = DataBuffer.allocate(128);
			byte[] temp = new byte[1024];
			do {
				int len = fis.read(temp);
				if (len == -1){
					break;
				}
				buffer.put(temp,0,len);
			}while(true);
			fis.close();
			String str = new String(buffer.arrayToPosition(),"UTF-8");
			data(request,response,str);
		}catch (Exception e) {
			e.printStackTrace();
			data(request,response,"fail");
		}
	}

}
