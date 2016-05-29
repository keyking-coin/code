package com.keyking.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.http.handler.HttpHandler;
import com.keyking.http.request.HttpRequestMessage;
import com.keyking.http.response.HttpResponseMessage;
import com.keyking.um.data.UserCharacter;
import com.keyking.util.JsonUtil;
import com.keyking.util.ServerLog;

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
				datas.put("result","OK");
				String str = JsonUtil.ObjectToJsonString(datas);
				response.appendBody(formatJosn(request,str));
				ServerLog.info(account + " login");
			}else{
				message(request,response,"密码错误");
			}
		}else{
			message(request, response,"账号不存在");
		}
	}
}
