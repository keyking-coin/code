package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformUserData;
import com.keyking.coin.util.JsonUtil;

public class HttpLogin extends HttpHandler {
	//http://139.196.30.53:32104/HttpLogin?account=13856094894&pwd=123456789
	@Override
	public void handle(HttpRequestMessage request,HttpResponseMessage response) {
        response.setContentType("text/plain");
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account = request.getParameter("account");  
		String pwd     = request.getParameter("pwd");
        UserCharacter user = CTRL.search(account);
        
		if (user != null){
			if (user.getPwd().equals(pwd)){
				Map<String,Object> datas = new HashMap<String,Object>();
				TransformUserData http_user= new TransformUserData(user);
				datas.put("result","OK");
				datas.put("datas",http_user);
				String str = JsonUtil.ObjectToJsonString(datas);
				response.appendBody(formatJosn(request,str));
			}else{
				message(request,response,"密码错误");
			}
		}else{
			message(request, response,"账号不存在");
		}
	}
}
