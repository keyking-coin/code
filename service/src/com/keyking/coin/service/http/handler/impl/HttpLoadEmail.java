package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformEmail;
import com.keyking.coin.util.JsonUtil;

public class HttpLoadEmail extends HttpHandler {
	//http://139.196.30.53:32104/HttpLoadEmail?uid=x&pwd=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
        long uid   = Long.parseLong(request.getParameter("uid"));
		String pwd = request.getParameter("pwd");
		UserCharacter user = CTRL.search(uid);
		if (!user.getPwd().equals(pwd)){
			message(request,response,"非法的请求");
			return;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			message(request,response,"您已经被封号原因是:" + forbidStr);
			return;
		}
		String str = null;
		List<Email> emails = user.getEmails();
		if (emails.size() > 0){
			List<TransformEmail> tfes = new ArrayList<TransformEmail>();
			for (Email email : emails){
				tfes.add(new TransformEmail(email,user));
			}
			Collections.sort(tfes);
			str = formatJosn(request,JsonUtil.ObjectToJsonString(tfes));
		}else{
			str = formatJosn(request,"[]");;
		}
		response.appendBody(str);
	}
}
