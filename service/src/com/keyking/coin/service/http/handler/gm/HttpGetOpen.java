package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.user.AccountApply;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpGetOpen extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long id = Long.parseLong(request.getParameter("id"));
		AccountApply apply = DB.getAccountApplyDao().search(id);
		if (apply != null){
			response.put("result","ok");
			response.put("apply",apply);
		}else{
			response.put("result","fail");
		}
	}

}
