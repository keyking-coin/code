package com.keyking.coin.service.http.handler.impl;

import java.io.File;
import java.io.FileOutputStream;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpSuspendUp extends HttpHandler {
	//http://139.196.30.53:32104/HttpSuspendUp
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		try {
			response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			String str = request.getPostValue();
			File file = new File("./suspend.data");
			if (!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(str.getBytes("UTF-8"));
			fos.flush();
			fos.close();
			message(request,response,"ok");
		}catch (Exception e) {
			e.printStackTrace();
			message(request,response,"fail");
		}
	}
}
