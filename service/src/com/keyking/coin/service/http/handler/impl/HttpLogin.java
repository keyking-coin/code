package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.data.HttpUserCharacterData;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;

public class HttpLogin extends HttpHandler {
	//http://139.196.30.53:32104/HttpLogin?account=13856094894&pwd=123456789
	@Override
	public void handle(HttpRequestMessage request,HttpResponseMessage response) {
		String account = request.getParameter("account");  
		String pwd     = request.getParameter("pwd");
        response.setContentType("text/plain");
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
        UserCharacter user = CTRL.search(account);
        Map<String,Object> datas = new HashMap<String,Object>();
		if (user != null){
			if (pwd.equals(pwd)){
				HttpUserCharacterData http_user= new HttpUserCharacterData(user);
				datas.put("result","OK");
				datas.put("datas",http_user);
			}else{
				datas.put("result","密码错误");
			}
		}else{
			datas.put("result","账号不存在");
		}
		response.appendBody(formatJosn(request,JsonUtil.ObjectToJsonString(datas)));
	}
}
