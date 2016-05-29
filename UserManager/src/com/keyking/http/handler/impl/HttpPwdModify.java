package com.keyking.http.handler.impl;

import com.keyking.http.handler.HttpHandler;
import com.keyking.http.request.HttpRequestMessage;
import com.keyking.http.response.HttpResponseMessage;
import com.keyking.um.data.UserCharacter;

public class HttpPwdModify extends HttpHandler {
	//http://139.196.30.53:32104/HttpPwdModify?account=x&op=x&np=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account = request.getParameter("account");
		String oldPwd = request.getParameter("op");
		String newPwd = request.getParameter("np");
		UserCharacter user = CTRL.search(account);
		if (user != null){
			if (!user.getPwd().equals(oldPwd)){
				message(request,response,"旧密码错误");
			}else{
				user.setPwd(newPwd);
				user.save();
				message(request,response,"ok");
			}
		}else{
			message(request,response,"账号不存在");
		}
	}
}
