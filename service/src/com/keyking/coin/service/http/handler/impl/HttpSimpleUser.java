package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformSimpleUserData;
import com.keyking.coin.util.JsonUtil;

public class HttpSimpleUser extends HttpHandler {
	//http://139.196.30.53:32104/HttpSimpleUser?uid=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
        long uid  = Long.parseLong(request.getParameter("uid"));
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			TransformSimpleUserData tsu = new TransformSimpleUserData(user);
			Map<String,Object> datas = new HashMap<String,Object>();
			datas.put("result","OK");
			datas.put("user",tsu);
			String str = JsonUtil.ObjectToJsonString(datas);
			response.appendBody(formatJosn(request,str));
		}else{
			message(request, response,"账号不存在");
		}
	}

}
