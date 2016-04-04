package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpRegist extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account   = request.getParameter("account");
		String pwd       = request.getParameter("pwd");
		String nickname  = request.getParameter("nick");
		String name      = request.getParameter("name");
		String address   = request.getParameter("address");
		String code      = request.getParameter("code");
		int codeResult = TOKEN.check(account,code);
		if (codeResult == 1){
			message(request,response,"验证码错误");
			return;
		}else if (codeResult == 2){
			message(request,response,"验证码已失效");
			return;
		}
		String registTime = TimeUtils.nowChStr();
		String result = CTRL.checkHttpAccout(account,nickname);
		if (result == null){
			UserCharacter user = new UserCharacter();
			user.setAccount(account);
			user.setPwd(pwd);
			user.setNikeName(nickname);
			if (!StringUtil.isNull(name)){
				user.setName(name);
			}
			if (!StringUtil.isNull(address)){
				user.addAddress(address);
			}
			user.setRegistTime(registTime);
			CTRL.register(user);
			message(request,response,"ok");
			ServerLog.info(account + " regist at " + registTime);
		}else{
			message(request,response,result);
		}
	}
}
