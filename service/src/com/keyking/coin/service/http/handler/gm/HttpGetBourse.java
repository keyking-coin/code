package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.bourse.BourseInfo;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpGetBourse extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String name = request.getParameter("name");
		BourseInfo info = DB.getBourseDao().search(name);
		if (info != null){
			response.put("result","ok");
			response.put("info",info);
		}else{
			response.put("result","fail");
		}
	}

}
