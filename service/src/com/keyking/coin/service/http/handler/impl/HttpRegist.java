package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.TimeUtils;

public class HttpRegist implements HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		String account   = request.getParameter("account");
		String pwd       = request.getParameter("pwd");
		String nickname  = request.getParameter("nick");
		String name      = request.getParameter("name");
		String address   = request.getParameter("address");
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String registTime = TimeUtils.nowChStr();
		String result = CTRL.checkHttpAccout(account,nickname);
		if ( result == null){
			UserCharacter user = new UserCharacter();
			user.setAccount(account);
			user.setPwd(pwd);
			user.setNikeName(nickname);
			user.setName(name);
			user.addAddress(address);
			user.setRegistTime(registTime);
			CTRL.register(user);
			response.appendBody("{\"result\":\"ok\"}");
		}else{
			response.appendBody("{\"result\":\"" + result + "\"}");
		}
	}
}