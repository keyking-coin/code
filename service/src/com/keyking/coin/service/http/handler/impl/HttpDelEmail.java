package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;

public class HttpDelEmail extends HttpHandler {
	//http://139.196.30.53:32104/HttpDelEmail?uid=x&pwd=x&id=1
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
        long uid   = Long.parseLong(request.getParameter("uid"));
		String pwd = request.getParameter("pwd");
		String ids = request.getParameter("ids");//要删除的邮件的编号id(支持删除多个id1,id2,id3...idn)
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
		synchronized (user) {
			if (user.removeEmail(ids)){
				user.save();
				Map<String,Object> datas = new HashMap<String,Object>();
				datas.put("result","ok");
				datas.put("num",user.getEmails().size());
				String str = formatJosn(request,JsonUtil.ObjectToJsonString(datas));
				response.appendBody(str);
			}
		}
	}

}
