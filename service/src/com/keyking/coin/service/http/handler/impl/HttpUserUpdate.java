package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;

public class HttpUserUpdate extends HttpHandler {
	//http://139.196.30.53:32104/HttpUserUpdate?uid=x&pwd=x&face=x&signa=x&push=x&name=x&identity=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid = Long.parseLong(request.getParameter("uid"));
		String pwd       = request.getParameter("pwd");
		String face      = request.getParameter("face");
		String signature = request.getParameter("signa");
		String pushStr   = request.getParameter("push");
		String name      = request.getParameter("name");
		String identity  = request.getParameter("identity");
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				message(request,response,"您已经被封号原因是:" + forbidStr);
				return;
			}
			if (!user.getPwd().equals(pwd)){
				message(request,response,"非法的请求");
				return;
			}
			if (!StringUtil.isNull(face)){
				user.setFace(face);
			}
			if (!StringUtil.isNull(signature)){
				user.setSignature(signature);
			}
			if (!StringUtil.isNull(pushStr)){
				byte push = Byte.parseByte(pushStr);
				user.setPush(push);
			}
			if (!StringUtil.isNull(name)){
				user.setName(name);
			}
			if (!StringUtil.isNull(identity)){
				user.setIdentity(identity);
			}
			user.setNeedSave(true);
			user.save();
			message(request,response,"ok");
			ServerLog.info(user.getAccount() + " update userInfo ");
		}else{
			message(request,response,"找不到用户信息");
		}
	}

}
