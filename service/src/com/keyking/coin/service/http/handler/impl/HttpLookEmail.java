package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpLookEmail extends HttpHandler {
	//http://139.196.30.53:32104/HttpLookEmail?uid=1&pwd=x&eid=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid   = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd = request.getParameter("pwd");//验证码
		long eid   = Long.parseLong(request.getParameter("eid"));//阅读邮件的编号
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			message(request,response,"您已经被封号原因是:" + forbidStr);
			return;
		}
		if (!user.getPwd().equals(pwd)){
			message(request,response,"非法的请求");
			return;
		}
		Email email = user.searchEmail(eid);
		if (email != null){
			email.setStatus((byte)1);
			email.setNeedSave(true);
			message(request,response,"ok");
		}else{
			message(request,response,"找不到邮件");
		}
	}

}
