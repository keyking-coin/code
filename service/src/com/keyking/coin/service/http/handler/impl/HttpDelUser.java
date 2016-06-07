package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpDelUser extends HttpHandler {
	//http://139.196.30.53:32109/HttpFindPwd?account=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account = request.getParameter("account");
		if (CTRL.removeUser(account)){
			message(request,response,"成功删除用户");
		}else{
			message(request,response,"账号错误");
		}
	}

}
