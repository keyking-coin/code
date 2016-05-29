package com.keyking.http.handler.impl;

import com.keyking.http.handler.HttpHandler;
import com.keyking.http.request.HttpRequestMessage;
import com.keyking.http.response.HttpResponseMessage;
import com.keyking.um.data.UserCharacter;
import com.keyking.util.ServerLog;
import com.keyking.util.StringUtil;
import com.keyking.util.TimeUtils;

public class HttpRegist extends HttpHandler {
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account   = request.getParameter("account");
		String pwd       = request.getParameter("pwd");
		String name      = request.getParameter("name");
		String fid       = request.getParameter("fid");
		String registTime = TimeUtils.nowChStr();
		String result = CTRL.checkHttpAccout(account);
		if (result == null){
			long lfid = Long.parseLong(fid);
			UserCharacter user = new UserCharacter();
			user.setAccount(account);
			user.setPwd(pwd);
			user.setFid(lfid);
			user.setRegistTime(registTime);
			if (!StringUtil.isNull(name)){
				user.setName(name);
			}
			CTRL.register(user);
			user.save();
			message(request,response,"ok");
			ServerLog.info(account + " regist at " + registTime);
		}else{
			message(request,response,result);
		}
	}
}
