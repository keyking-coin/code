package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.ad.ADEntity;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpGetAd extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long id = Long.parseLong(request.getParameter("id"));
		ADEntity ade = DB.getAdDao().search(id);
		if (ade != null){
			response.put("result","ok");
			response.put("ad",ade);
		}else{
			response.put("result","fail");
		}
	}

}
