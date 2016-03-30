package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpFindPwd extends HttpHandler {
	//http://139.196.30.53:32104/HttpFindPwd?tel=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account = request.getParameter("tel");
		UserCharacter user = CTRL.search(account);
		if (!SMS.couldSend(account)){
			message(request,response,"近期您刚取回过密码,请稍候");
			return;
		}
		if (user != null && SMS.sendPassword(account,user.getPwd())){
			message(request,response,"ok");
		}else{
			message(request,response,"账号不存在");
		}
	}
}
